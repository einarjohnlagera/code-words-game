package com.cctalents.code_words.dto;

import com.cctalents.code_words.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class LeaderBoardResponse extends GameResponse {

    public interface LeaderBoardsView extends BasicView {}

    @JsonView(LeaderBoardsView.class)
    private String playerName;
    @JsonView(LeaderBoardsView.class)
    private Difficulty difficulty;
}
