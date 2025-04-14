package com.cctalents.code_words.exception;

public class NoGameFoundException extends RuntimeException {

    public NoGameFoundException(Long id) {
        super(String.format("Game not found with id %d", id));
    }
}
