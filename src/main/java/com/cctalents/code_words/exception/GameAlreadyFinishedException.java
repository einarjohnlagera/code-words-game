package com.cctalents.code_words.exception;

public class GameAlreadyFinishedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Game already finished";

    public GameAlreadyFinishedException() {
        super(DEFAULT_MESSAGE);
    }
}
