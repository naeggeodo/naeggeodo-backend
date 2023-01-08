package com.naeggeodo.util;

import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.exception.ErrorCode;

public class ResponseUtils {

    private final static String DEFAULT_SUCCESS_MESSAGE = "정상처리 되었습니다.";

    public static <T> ResponseDTO success(T data) {
        return ResponseDTO.builder()
                .success(true)
                .data(data)
                .message(DEFAULT_SUCCESS_MESSAGE)
                .status(200)
                .build();
    }
    public static ResponseDTO created() {
        return ResponseDTO.builder()
                .success(true)
                .message(DEFAULT_SUCCESS_MESSAGE)
                .status(201)
                .build();
    }

    public static ResponseDTO error(ErrorCode errorCode) {
        return ResponseDTO.builder()
                .success(false)
                .data(null)
                .message(errorCode.getDetail())
                .status(errorCode.getHttpStatus().value())
                .errorCode(errorCode.name())
                .build();
    }
}
