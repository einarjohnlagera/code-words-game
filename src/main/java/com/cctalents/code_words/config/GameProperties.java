package com.cctalents.code_words.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GameProperties {

    @Value("${game.default-player:Guest}")
    private String defaultPlayer;
    @Value("${game.default-difficulty:EASY}")
    private String defaultDifficulty;
    @Value("${game.allowed-attempts:6}")
    private int allowedAttempts;

}
