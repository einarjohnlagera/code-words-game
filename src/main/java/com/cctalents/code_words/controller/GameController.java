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

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService service;
    private final WordService wordService;

    @PostMapping
    @JsonView(GameResponse.CreateGameView.class)
    public GameResponse createGame(@RequestBody CreateGameRequest request) {
        return toGameResponse(service.createGame(request));
    }

    @PostMapping("/{gameId}/guess")
    @JsonView(GameResponse.GuessView.class)
    public GameResponse guessWord(@PathVariable Long gameId,
                                  @RequestBody @Valid GameRequest request) {
        return toGameResponse(service.guess(gameId, request));
    }

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
