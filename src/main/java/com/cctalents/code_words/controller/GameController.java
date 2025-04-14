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
    @JsonView(GameResponse.WithIdView.class)
    public GameResponse createGame(@RequestBody CreateGameRequest request) {
        Game game = service.createGame(request);
        return mapFrom(game);
    }

    @PostMapping("/{gameId}/guess")
    @JsonView(GameResponse.WithIdView.class)
    public GameResponse guessWord(@PathVariable Long gameId,
                                  @RequestBody @Valid GameRequest request) throws NoGameFoundException {
        Game game = service.guess(gameId, request);
        return mapFrom(game);
    }

    @GetMapping("/{gameId}")
    @JsonView(GameResponse.GameView.class)
    public GameResponse getGame(@PathVariable Long gameId) throws NoGameFoundException {
        Game game = service.findGameById(gameId);
        return mapFrom(game);
    }

    private GameResponse mapFrom(Game game) {
        return GameResponse.builder()
                .gameId(game.getId())
                .maskedWord(GameUtil.spacesInBetween(game.getMaskedWord()))
                .remainingAttempts(game.getRemainingAttempts())
                .status(game.getStatus())
                .build();
    }
}
