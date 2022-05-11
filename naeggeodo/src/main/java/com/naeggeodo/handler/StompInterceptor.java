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
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.service.ChatUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor{
	
	private final ChatUserRepository chatUserRepository;
	private final ChatMainRepository chatMainRepository;
	private final SessionHandler sessionHandler;
	
	@Override
	@Transactional
	public Message<?> preSend(Message<?> message, MessageChannel channel){
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
		//접속 인터셉트해서 조건에 따라 exception 발생시키기 
		if(StompCommand.CONNECT.equals(headers.getCommand())) {
			log.info("CONNECT");
			System.out.println("========================================================");
			Long chatMain_id = Long.parseLong(headers.getNativeHeader("chatMain_id").get(0));
			String sender = headers.getNativeHeader("sender").get(0);
			String newSessionId = headers.getSessionId();
			
			ChatMain chatMain =  chatMainRepository.findChatMainEntityGraph(chatMain_id);
			ChatUser enteredChatUser = null;
			if(!chatMain.getChatUser().isEmpty()) {
				
				for (ChatUser chatUser : chatMain.getChatUser()) {
					if(chatUser.getUser().getId().equals(sender)) {
						enteredChatUser = chatUser;
					}
				}
			}
			
			//ChatUser chatUser = chatUserRepository.findByChatMainIdAndUserId(chatMain_id, sender);
			if(!ChatState.CREATE.equals(chatMain.getState())) {
				log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
				throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
			}
			if(enteredChatUser != null) {
				String oldSessionId = enteredChatUser.getSessionId();
				if(sessionHandler.isExist(oldSessionId)) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.SESSION_DUPLICATION);
					throw new CustomWebSocketException(StompErrorCode.SESSION_DUPLICATION.name());
				}
				//chatUserRepository.updateSessionId(headers.getSessionId(), sessionId);
				enteredChatUser.setSessionId(newSessionId);
			}
		}
		System.out.println("=========================end===============================");
		return message;
	}
	
}
