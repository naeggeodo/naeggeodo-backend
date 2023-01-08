package com.naeggeodo.exception;

import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.util.ResponseUtils;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomHttpException.class})
    protected ResponseDTO handleCustomException(CustomHttpException e) {
        return ResponseUtils.error(e.getErrorCode());
    }

    @ExceptionHandler(value = {NumberFormatException.class, NullPointerException.class})
    protected ResponseDTO handleNumberFormatException(Exception e) {
        return ResponseUtils.error(ErrorCode.INVALID_FORMAT);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseDTO handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseUtils.error(ErrorCode.INVALID_FORMAT);
    }

    @ExceptionHandler(value = {NoResultException.class, EntityNotFoundException.class})
    protected ResponseDTO handleNoResultException() {
        return ResponseUtils.error(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    protected ResponseDTO handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseUtils.error(ErrorCode.INVALID_FORMAT);
    }

    @ExceptionHandler(value = {MalformedJwtException.class})
    protected ResponseDTO handleHttpMalformedJwtException() {
        return ResponseUtils.error(ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseDTO handleException(Exception e) {
        log.warn("Unhandled Exception From GlobalExceptionHandler");
        e.printStackTrace();
        return ResponseUtils.error(ErrorCode.UNKNOWN_ERROR);
    }

}
