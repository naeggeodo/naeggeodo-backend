package com.naeggeodo.exception;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Getter
public class CustomWebSocketException extends RuntimeException{

	//private final StompErrorCode errorCode;

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
	public CustomWebSocketException(String msg, StompHeaderAccessor headers,Long chatMain_id){
		super(msg);
		this.chatMain_id = chatMain_id;
		this.headers = headers;
	}
}
