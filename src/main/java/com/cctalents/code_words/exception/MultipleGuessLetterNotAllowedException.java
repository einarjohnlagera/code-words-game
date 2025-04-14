package com.cctalents.code_words.exception;

public class MultipleGuessLetterNotAllowedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You can only guess by a letter or the full word";

    public MultipleGuessLetterNotAllowedException() {
        super(DEFAULT_MESSAGE);
    }
}
