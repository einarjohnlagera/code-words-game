package com.cctalents.code_words.controller;

import com.cctalents.code_words.dto.CreateGameRequest;
import com.cctalents.code_words.dto.GameRequest;
import com.cctalents.code_words.dto.GameResponse;
import com.cctalents.code_words.exception.NoGameFoundException;
import com.cctalents.code_words.service.GameService;
import com.fasterxml.jackson.annotation.JsonView;
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
        return service.createGame(request);
    }

    @PostMapping("/{gameId}/guess")
    @JsonView(GameResponse.WithIdView.class)
    public GameResponse guessWord(@PathVariable Long gameId, @RequestBody GameRequest request) throws NoGameFoundException {
        return service.guess(gameId, request);
    }

    @GetMapping("/{gameId}")
    @JsonView(GameResponse.GameView.class)
    public GameResponse getGame(@PathVariable String gameId) {
        return GameResponse.builder().build();
    }
}
