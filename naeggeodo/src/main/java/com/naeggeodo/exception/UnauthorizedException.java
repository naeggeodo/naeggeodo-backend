package com.naeggeodo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class UnauthorizedException extends RuntimeException{

	public UnauthorizedException(ErrorCode errorCode) {
	}
	
	public UnauthorizedException(String msg) {
		super(msg);
	}
}
