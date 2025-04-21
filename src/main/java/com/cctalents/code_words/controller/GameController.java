package com.cctalents.code_words.controller;

import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.dto.GameResponse;
import com.cctalents.code_words.dto.LeaderBoardResponse;
import com.cctalents.code_words.entity.Game;
import com.cctalents.code_words.entity.Word;
import com.cctalents.code_words.enums.Difficulty;
import com.cctalents.code_words.service.GameService;
import com.cctalents.code_words.service.WordService;
import com.cctalents.code_words.util.GameUtil;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GameController is responsible for handling HTTP requests related to managing game sessions.
 * It exposes endpoints for creating a new game, making guesses, retrieving game state,
 * forfeiting games, and fetching leaderboard information.
 * <p>
 * This controller delegates game-related business logic to the GameService
 * and word-related operations to the WordService.
 * </p>
 */
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService service;
    private final WordService wordService;

    /**
     * Creates a new game session based on the provided request.
     * The game initialization includes setting up a player name, game difficulty,
     * selecting a random word based on difficulty, masking the word, and initializing
     * the remaining attempts.
     *
     * @param request the object containing the game initialization parameters such as player name
     *                and difficulty level
     * @return the response containing the initialized game details, including the game's ID,
     *         masked word, remaining attempts, and game status
     */
    @PostMapping
    @JsonView(GameResponse.CreateGameView.class)
    public GameResponse createGame(@RequestBody CreateGameRequest request) {
        return toGameResponse(service.createGame(request));
    }

    /**
     * Processes the player's guess for a specific game based on the provided game ID and guess input.
     * Updates the game state including the masked word, the number of remaining attempts, and the
     * game status depending on whether the guess is correct, partially correct, or incorrect.
     *
     * @param gameId the unique identifier of the game for which the guess is being made
     * @param request the object containing the user's guess, validated to ensure that the input is not null
     * @return the response object representing the updated game state, including properties such as
     *         the game's ID, updated masked word, remaining attempts, and the current game status
     */
    @PostMapping("/{gameId}/guess")
    @JsonView(GameResponse.GuessView.class)
    public GameResponse guessWord(@PathVariable Long gameId,
                                  @RequestBody @Valid GameRequest request) {
        return toGameResponse(service.guess(gameId, request));
    }

    /**
     * Retrieves the current state of a specific game using its unique identifier.
     *
     * @param gameId the unique identifier of the game to be retrieved
     * @return the response object representing the current state of the game, including
     *         the game's ID, masked word, remaining attempts, and game status
     */
    @GetMapping("/{gameId}")
    @JsonView(GameResponse.GameStateView.class)
    public GameResponse getGame(@PathVariable Long gameId) {
        return toGameResponse(service.findGameById(gameId));
    }

    @PostMapping("{gameId}/forfeit")
    @JsonView(GameResponse.GuessView.class)
    public GameResponse forfeit(@PathVariable Long gameId) {
        return toGameResponse(service.forfeit(gameId));
    }

    /**
     * Retrieves a filtered list of leaderboard entries categorized by difficulty level.
     * The leaderboard includes a limited number of top players for each difficulty, sorted
     * by the number of remaining attempts in descending order. Only up to 5 top players are
     * retained for each difficulty category.
     *
     * @return a list of {@code LeaderBoardResponse} objects representing the leaderboard
     * data across different difficulty levels, including player names, difficulties, and
     * remaining attempts
     */
    @GetMapping("/leaderboards")
    @JsonView(LeaderBoardResponse.LeaderBoardsView.class)
    public List<LeaderBoardResponse> getLeaderboards() {
        List<LeaderBoardResponse> topPlayers = service.getTopPlayers().stream()
                .sorted(Comparator.comparingInt(Game::getRemainingAttempts).reversed())
                .map(this::toLeaderBoardsResponse)
                .toList();

        Map<Difficulty, List<LeaderBoardResponse>> byDifficulty = topPlayers.stream()
                .collect(Collectors.groupingBy(LeaderBoardResponse::getDifficulty));

        List<LeaderBoardResponse> result = new ArrayList<>();
        for (Map.Entry<Difficulty, List<LeaderBoardResponse>> entry : byDifficulty.entrySet()) {
            // should just limit leaderboards per difficulty to 5
            result.addAll(entry.getValue().stream()
                    .limit(5)
                    .toList());
        }

        return result;
    }

    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getId())
                .maskedWord(GameUtil.spacesInBetween(game.getMaskedWord()))
                .remainingAttempts(game.getRemainingAttempts())
                .status(game.getStatus())
                .build();
    }

    private LeaderBoardResponse toLeaderBoardsResponse(Game game) {
        Word word = wordService.getWordByName(game.getWord());
        LeaderBoardResponse result = new LeaderBoardResponse(game.getPlayer(), word.getDifficulty());
        result.setMaskedWord(GameUtil.spacesInBetween(game.getMaskedWord()));
        result.setRemainingAttempts(game.getRemainingAttempts());

        return result;
    }
}
