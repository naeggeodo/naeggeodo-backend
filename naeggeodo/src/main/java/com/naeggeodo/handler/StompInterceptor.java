package com.naeggeodo.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.exception.StompErrorCode;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.exception.CustomWebSocketException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor{

	private final ChatMainRepository chatMainRepository;
	private final SessionHandler sessionHandler;
	
	@Override
	@Transactional
	public Message<?> preSend(Message<?> message, MessageChannel channel){
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
		//접속 인터셉트해서 조건에 따라 exception 발생시키기 
		if(StompCommand.CONNECT.equals(headers.getCommand())) {
			log.info("CONNECT");
			System.out.println("==========================connect==============================");
			Long chatMain_id = null;
			String sender = null;
			try {
				chatMain_id = Long.parseLong(headers.getNativeHeader("chatMain_id").get(0));
				sender = headers.getNativeHeader("sender").get(0);
			} catch (NumberFormatException | NullPointerException e) {
				throw new CustomWebSocketException(StompErrorCode.BAD_REQUEST.name());
			}
			String newSessionId = headers.getSessionId();
			
			ChatMain chatMain =  chatMainRepository.findChatMainEntityGraph(chatMain_id);
			ChatUser enteredChatUser = chatMain.findChatUserBySender(sender);
			
			
			if(chatMain.isFull()) {
				if(enteredChatUser != null) {
					String oldSessionId = enteredChatUser.getSessionId();
					if(sessionHandler.isExist(oldSessionId)) {
						log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.SESSION_DUPLICATION);
						throw new CustomWebSocketException(StompErrorCode.SESSION_DUPLICATION.name());
					}
					enteredChatUser.setSessionId(newSessionId);
				} else {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
					throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
				}
			}
			
			if(!chatMain.canEnter()) {
				log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
				throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
			}
			
			if(enteredChatUser !=null) {
				if(BanState.BANNED.equals(enteredChatUser.getBanState())) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
					throw new CustomWebSocketException(StompErrorCode.BANNED_CHAT_USER.name());
				}
			}
		}
		System.out.println("=========================end===============================");
		return message;
	}
	

}
