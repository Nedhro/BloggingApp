package com.prodev.bloggingservice.util;

public class AlreadyExistException extends Exception{

    private static final long serialVersionUID = 1L;

    public AlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
