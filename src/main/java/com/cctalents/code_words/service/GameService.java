package com.cctalents.code_words.service;

import com.cctalents.code_words.config.GameProperties;
import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.dto.GameResponse;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameProperties gameProperties;
    private final WordService wordService;
    private final GameRepository repository;

    public GameResponse createGame(CreateGameRequest request) {
        if (!StringUtils.hasLength(request.getDifficulty())) {
            request.setDifficulty(gameProperties.getDefaultDifficulty());
        }
        if (!StringUtils.hasText(request.getPlayer())) {
            request.setPlayer(gameProperties.getDefaultPlayer());
        }

        String word = wordService.getRandomWordByDiffuculty(Difficulty.valueOf(request.getDifficulty()));
        String maskedWord = GameUtil.mask(word);
        int remainingAttempts = gameProperties.getAllowedAttempts();
        Game game = Game.builder()
                .word(word)
                .maskedWord(maskedWord)
                .remainingAttempts(remainingAttempts)
                .player(request.getPlayer())
                .build();
        Long gameId = repository.save(game).getId();

        return GameResponse.builder()
                .gameId(gameId)
                .maskedWord(maskedWord)
                .remainingAttempts(remainingAttempts)
                .build();
    }

    public Game findGameById(Long gameId) {
        return repository.findById(gameId).orElse(null);
    }

    public GameResponse guess(Long gameId, GameRequest gameRequest) throws NoGameFoundException {
        String guess = gameRequest.getGuess();
        Game game = Optional.ofNullable(findGameById(gameId))
                .orElseThrow(() -> new NoGameFoundException(gameId));
       validateGameStatus(game);

        // check if user was able to guess the word
        if (game.getWord().equals(guess)) {
            game.setStatus(GameStatus.WON);
        } else if (guess.length() > 1) {
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
        return GameUtil.mapFrom(game);
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
