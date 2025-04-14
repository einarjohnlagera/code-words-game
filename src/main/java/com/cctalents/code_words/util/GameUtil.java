package com.cctalents.code_words.util;

import com.cctalents.code_words.dto.GameResponse;
import com.cctalents.code_words.entity.Game;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GameUtil {

    public static String mask(String str) {
        return str.replaceAll("[^a-zA-Z]", "_ ").trim();
    }

    public static GameResponse mapFrom(Game game) {
        return GameResponse.builder()
                .gameId(game.getId())
                .maskedWord(game.getWord())
                .remainingAttempts(game.getRemainingAttempts())
                .status(game.getStatus())
                .build();
    }
}
