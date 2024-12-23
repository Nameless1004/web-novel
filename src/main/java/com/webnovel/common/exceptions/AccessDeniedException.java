package com.webnovel.common.exceptions;

public class AccessDeniedException extends ServiceException{

    public AccessDeniedException(String message) {
        super(message);
    }
}
