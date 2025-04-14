package com.cctalents.code_words.exception;

public class MultipleGuessLetterNotAllowedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Multiple guess letters are not allowed";

    public MultipleGuessLetterNotAllowedException() {
        super(DEFAULT_MESSAGE);
    }
}
