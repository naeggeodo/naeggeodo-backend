package com.naeggeodo.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(ErrorCode errorCode) {
    }

    public UnauthorizedException(String msg) {
        super(msg);
    }
}
