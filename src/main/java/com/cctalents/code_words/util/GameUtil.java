package com.cctalents.code_words.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GameUtil {

    public static String mask(String str) {
        return str.replaceAll("[a-zA-Z]", "_");
    }

    public static String spacesInBetween(String str) {
        return String.join(" ", str.split(""));
    }
}
