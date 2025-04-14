package com.cctalents.code_words.util;

import java.util.Arrays;

public class EnumUtil {

    public static boolean equalsAny(Enum<?> e, Enum<?>... any) {
        return Arrays.stream(any)
                .anyMatch(anEnum -> anEnum == e);
    }
}
