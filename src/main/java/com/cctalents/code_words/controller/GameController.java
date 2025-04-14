package com.cctalents.code_words.controller;

import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.dto.GameResponse;
import com.cctalents.code_words.entity.Game;
import com.cctalents.code_words.exception.NoGameFoundException;
import com.cctalents.code_words.service.GameService;
import com.cctalents.code_words.util.GameUtil;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService service;

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

    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getId())
                .maskedWord(GameUtil.spacesInBetween(game.getMaskedWord()))
                .remainingAttempts(game.getRemainingAttempts())
                .status(game.getStatus())
                .build();
    }
}
