package com.naeggeodo.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.naeggeodo.exception.StompErrorCode;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.service.ChatUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor{
	
	private final ChatMainService chatMainService;
	private final ChatUserService chatUserService;
	private final SessionHandler sessionHandler;
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel){
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
		
		//접속 인터셉트해서 조건에 따라 exception 발생시키기 
		if(StompCommand.CONNECT.equals(headers.getCommand())) {
			log.info("CONNECT");
			Long chatMain_id = Long.parseLong(headers.getNativeHeader("chatMain_id").get(0));
			String sender = headers.getNativeHeader("sender").get(0);
			
			if(!ChatState.CREATE.equals(chatMainService.getState(chatMain_id))) {
				log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
				throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
			}
			
			if(chatUserService.isExist(chatMain_id,sender)) {
				String sessionId = chatUserService.getSession_id(chatMain_id, sender);
				if(sessionHandler.isExist(sessionId)) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.SESSION_DUPLICATION);
					throw new CustomWebSocketException(StompErrorCode.SESSION_DUPLICATION.name());
				}
				chatUserService.updateSession_id(chatMain_id, sender, headers.getSessionId());
			}
		}
		
		return message;
	}
	
}
