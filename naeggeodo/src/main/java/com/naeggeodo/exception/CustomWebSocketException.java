package com.naeggeodo.exception;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Getter
public class CustomWebSocketException extends RuntimeException{


	private String msg;
	private StompHeaderAccessor headers;
	private Long chatMain_id;
	public CustomWebSocketException(String msg) {
		super(msg);
	}
	public CustomWebSocketException(String msg, StompHeaderAccessor headers){
		super(msg);
		this.headers = headers;
	}
}
