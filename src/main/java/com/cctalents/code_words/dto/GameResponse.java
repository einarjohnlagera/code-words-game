package com.cctalents.code_words.dto;

import com.cctalents.code_words.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    // views
    public interface BasicView {}
    public interface IdView {}
    public interface StatusView {}

    // views by api
    public interface CreateGameView extends IdView, BasicView {}
    public interface GuessView extends IdView, BasicView, StatusView {}
    public interface GameStateView extends BasicView, StatusView {}

    @JsonView(IdView.class)
    private Long gameId;
    @JsonView(BasicView.class)
    private String maskedWord;
    @JsonView(BasicView.class)
    private int remainingAttempts;
    @JsonView(StatusView.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GameStatus status;
}
