package com.webnovel.common.exceptions;

public class InvalidRequestException extends ServiceException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
