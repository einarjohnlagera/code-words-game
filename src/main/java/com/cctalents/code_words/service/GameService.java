package com.cctalents.code_words.service;

import com.cctalents.code_words.config.GameProperties;
import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.entity.Game;
import com.cctalents.code_words.enums.Difficulty;
import com.cctalents.code_words.enums.GameStatus;
import com.cctalents.code_words.exception.GameAlreadyFinishedException;
import com.cctalents.code_words.exception.MultipleGuessLetterNotAllowedException;
import com.cctalents.code_words.exception.NoGameFoundException;
import com.cctalents.code_words.repository.GameRepository;
import com.cctalents.code_words.util.EnumUtil;
import com.cctalents.code_words.util.GameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing game-related operations, including creating games,
 * processing user guesses, handling forfeits, and retrieving top players.
 * <p>
 * This class interacts with the GameRepository for persistence, WordService for
 * retrieving words based on difficulty, and GameProperties for configurable
 * settings such as default difficulty and allowed attempts.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameProperties gameProperties;
    private final WordService wordService;
    private final GameRepository repository;

    /**
     * Creates a new game based on the provided {@link CreateGameRequest}.
     * If the difficulty or player fields in the request are not provided,
     * default values from the game properties will be used. The method initializes
     * a random word based on the specified difficulty, masks it, and assigns a
     * predefined number of remaining attempts.
     *
     * @param request the request object containing the player name and game difficulty
     * @return the created game entity after being saved in the repository
     */
    public Game createGame(CreateGameRequest request) {
        if (!StringUtils.hasLength(request.getDifficulty())) {
            request.setDifficulty(gameProperties.getDefaultDifficulty());
        }
        if (!StringUtils.hasText(request.getPlayer())) {
            request.setPlayer(gameProperties.getDefaultPlayer());
        }

        String word = wordService.getRandomWordByDifficulty(Difficulty.valueOf(request.getDifficulty()));
        String maskedWord = GameUtil.mask(word);
        int remainingAttempts = gameProperties.getAllowedAttempts();
        Game game = Game.builder()
                .word(word)
                .maskedWord(maskedWord)
                .remainingAttempts(remainingAttempts)
                .player(request.getPlayer())
                .build();
        return repository.save(game);
    }

    public Game findGameById(Long gameId) {
        return repository.findById(gameId).
                orElseThrow(() -> new NoGameFoundException(gameId));
    }

    /**
     * Processes a user's guess for a game, updating the game status and masked word
     * based on the guess. Throws an exception if an invalid guess is provided.
     *
     * @param gameId the unique identifier of the game
     * @param gameRequest the request containing the guess made by the user
     * @return the updated game state after processing the guess
     */
    public Game guess(Long gameId, GameRequest gameRequest) {
        String guess = gameRequest.getGuess();
        Game game = findGameById(gameId);
        validateGameStatus(game);

        // check if user was able to guess the word
        if (game.getWord().equals(guess)) {
            // masked word should already show the answer
            game.setMaskedWord(guess);
            game.setStatus(GameStatus.WON);
        } else if (guess.length() == 2) {
            throw new MultipleGuessLetterNotAllowedException();
        } else if (game.getWord().contains(guess)) {
            game.setMaskedWord(unmaskCorrect(game.getMaskedWord(), game.getWord(), guess));

            // check now if the user was able to fully unmasked the word
            if (game.getWord().equals(game.getMaskedWord())) {
                game.setStatus(GameStatus.WON);
            }
        } else {
            game.setRemainingAttempts(game.getRemainingAttempts() - 1);

            if (game.getRemainingAttempts() == 0) {
                game.setStatus(GameStatus.LOST);
            }
        }
        return repository.save(game);
    }

    /**
     * Marks the game with the given identifier as forfeited by setting its status to LOST.
     * The updated game instance is then saved in the repository and returned.
     *
     * @param gameId the unique identifier of the game to be forfeited
     * @return the updated game entity with its status set to LOST
     */
    public Game forfeit(Long gameId) {
        Game game = findGameById(gameId);
        validateGameStatus(game);
        game.setStatus(GameStatus.LOST);
        return repository.save(game);
    }

    /**
     * Retrieves a list of top players based on their game performance.
     * It fetches all games with a status of WON and ranks them in descending
     * order by the number of remaining attempts.
     *
     * @return a list of games sorted by remaining attempts in descending order,
     *         containing only the games with a status of WON
     */
    public List<Game> getTopPlayers() {
        List<Game> leaders = repository.findAllByStatus(GameStatus.WON);
        return leaders.stream()
                .sorted(Comparator.comparingInt(Game::getRemainingAttempts).reversed())
                .collect(Collectors.toList());
    }

    private void validateGameStatus(Game game) {
        // tagged the game as already in progress
        if (game.getStatus() == null) {
            game.setStatus(GameStatus.IN_PROGRESS);
        } else if (EnumUtil.equalsAny(game.getStatus(), GameStatus.WON, GameStatus.LOST)) {
            throw new GameAlreadyFinishedException();
        }
    }

    private String unmaskCorrect(String masked, String word, String guess) {
        StringBuilder result = new StringBuilder();
        // since we're only allowed to guess by a single letter
        char guessChar = guess.charAt(0);
        for (int index = 0; index < word.length(); index++) {
            if (word.charAt(index) == guessChar) {
                result.append(guessChar);
            } else {
                result.append(masked.charAt(index));
            }
        }

        return result.toString();
    }
}
