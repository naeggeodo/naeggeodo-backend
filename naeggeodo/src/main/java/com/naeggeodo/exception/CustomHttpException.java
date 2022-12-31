package com.naeggeodo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomHttpException extends RuntimeException {
    private final ErrorCode errorCode;
}
