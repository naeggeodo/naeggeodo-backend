package com.naeggeodo.exception;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = {CustomHttpException.class})
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomHttpException e){
		return ErrorResponse.toResponseEntity(e.getErrorCode());
	}
	
	@ExceptionHandler(value= {NumberFormatException.class,NullPointerException.class})
	protected ResponseEntity<ErrorResponse> handleNumberFormatException(Exception e){
		return ErrorResponse.toResponseEntity(ErrorCode.INVALID_FORMAT);
	}
	
	@ExceptionHandler(value= {IllegalArgumentException.class})
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
		return ErrorResponse.toResponseEntity(ErrorCode.INVALID_FORMAT);
	}
	
	@ExceptionHandler(value= {NoResultException.class, EntityNotFoundException.class})
	protected ResponseEntity<ErrorResponse> handleNoResultException(NoResultException e){
		return ErrorResponse.toResponseEntity(ErrorCode.RESOURCE_NOT_FOUND);
	}
	@ExceptionHandler(value= {HttpMediaTypeNotSupportedException.class})
	protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e){
		return ErrorResponse.toResponseEntity(ErrorCode.INVALID_FORMAT);
	}

	@ExceptionHandler(value= {Exception.class})
	protected ResponseEntity<ErrorResponse> handleException(Exception e){
		return ErrorResponse.toResponseEntity(ErrorCode.UNKNOWN_ERROR);
	}
	
}
