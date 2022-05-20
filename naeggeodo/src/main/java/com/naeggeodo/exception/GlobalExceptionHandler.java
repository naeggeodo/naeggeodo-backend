package com.naeggeodo.exception;

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
	
	@ExceptionHandler(value= {NumberFormatException.class})
	protected ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException e){
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value= {IllegalArgumentException.class})
	protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e){
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value= {NoResultException.class})
	protected ResponseEntity<ErrorResponse> handleNoResultException(NoResultException e){
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(value= {HttpMediaTypeNotSupportedException.class})
	protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e){
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	

	
}
