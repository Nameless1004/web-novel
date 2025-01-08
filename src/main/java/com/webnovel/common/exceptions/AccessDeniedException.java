package com.webnovel.common.exceptions;

public class AccessDeniedException extends AuthException{

    public AccessDeniedException() {
        super("권한이 없습니다.");
    }
}
