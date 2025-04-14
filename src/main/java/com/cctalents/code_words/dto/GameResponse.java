package com.cctalents.code_words.dto;

import com.cctalents.code_words.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameResponse {

    public interface GameView {}
    public interface WithIdView extends GameView {}

    @JsonView(WithIdView.class)
    private Long gameId;
    @JsonView(GameView.class)
    private String maskedWord;
    @JsonView(GameView.class)
    private int remainingAttempts;
    @JsonView(GameView.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GameStatus status;
}
