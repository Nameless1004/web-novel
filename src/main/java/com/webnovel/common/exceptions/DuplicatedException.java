package com.webnovel.common.exceptions;

public class DuplicatedException extends ServiceException{

    public DuplicatedException(String message) {
        super(message);
    }
}
