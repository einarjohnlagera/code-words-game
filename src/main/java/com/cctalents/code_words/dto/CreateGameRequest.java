package com.cctalents.code_words.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateGameRequest {

    private String player;
    private String difficulty;
}
