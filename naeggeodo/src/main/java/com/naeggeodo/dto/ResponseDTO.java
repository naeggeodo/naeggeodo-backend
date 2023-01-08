package com.naeggeodo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseDTO<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final boolean success;
    private final T data;
    private final String message;
    private final int status;
    private final String errorCode;


}
