package com.cctalents.code_words.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class GameRequest {

    @NotNull
    private String guess;
}
