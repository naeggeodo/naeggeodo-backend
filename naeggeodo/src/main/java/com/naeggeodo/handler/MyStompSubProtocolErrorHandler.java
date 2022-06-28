package com.naeggeodo.handler;


import com.naeggeodo.exception.CustomWebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
@Slf4j
public class MyStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler{

	@Override
	protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload,
			Throwable cause, StompHeaderAccessor clientHeaderAccessor) {
		if (cause != null && cause.getCause() instanceof CustomWebSocketException) {
			String errorCode = cause.getCause().getMessage();
			log.warn("errorCode = {}",errorCode);
			log.trace("stackTrace = ",cause);
			errorHeaderAccessor.setNativeHeader("message", errorCode);
		}
		return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
	}


	/*
	 * 	
	 * 
	 * */
	
}
