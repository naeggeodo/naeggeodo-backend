package com.naeggeodo.exception;

import lombok.Getter;

@Getter
public class CustomWebSocketException extends RuntimeException{

	//private final StompErrorCode errorCode;

	private String msg;
	public CustomWebSocketException(String msg) {
		super(msg);
	}
}
