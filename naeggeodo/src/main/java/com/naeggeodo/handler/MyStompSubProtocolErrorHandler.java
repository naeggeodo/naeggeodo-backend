package com.naeggeodo.handler;


import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;


import com.naeggeodo.exception.CustomWebSocketException;

@Component
public class MyStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler{

	@Override
	protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload,
			Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
		if(cause.getCause() instanceof CustomWebSocketException) {
			String errorCode = cause.getCause().getMessage();
			
			errorHeaderAccessor.setNativeHeader("message", errorCode);
			
		}
		return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
	}
	
	/*
	 * 	
	 * 
	 * */
	
}
