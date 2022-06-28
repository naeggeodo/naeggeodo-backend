package com.naeggeodo.handler;

import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.exception.StompErrorCode;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.repository.ChatMainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor{

	private final ChatMainRepository chatMainRepository;
	private final WebsocketSessionHandler sessionHandler;
	private final JwtTokenProvider jwtProvider;
	
	@Override
	@Transactional
	public Message<?> preSend(Message<?> message, MessageChannel channel){
		
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
		//접속 인터셉트해서 조건에 따라 exception 발생시키기 
		if(StompCommand.CONNECT.equals(headers.getCommand())) {

			//jwt 토큰 인증
			String token = null;
			if(headers.getNativeHeader("Authorization") != null){
				token = AuthorizationExtractor.extract(headers.getNativeHeader("Authorization").get(0));
			}

			if(Objects.isNull(token)) {
				throw new CustomWebSocketException(StompErrorCode.UNAUTHORIZED.name());
			}

			if(!jwtProvider.validateToken(token)) {
				throw new CustomWebSocketException(StompErrorCode.UNAUTHORIZED.name());
			}

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
				if(enteredChatUser == null) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
					throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
				}
			}
			
			if(!chatMain.canEnter()) {
				log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
				throw new CustomWebSocketException(StompErrorCode.INVALID_STATE.name());
			}
			
			if(enteredChatUser !=null) {
				String oldSessionId = enteredChatUser.getSessionId();
				if(BanState.BANNED.equals(enteredChatUser.getBanState())) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.INVALID_STATE);
					throw new CustomWebSocketException(StompErrorCode.BANNED_CHAT_USER.name());
				}
				if(sessionHandler.isExist(oldSessionId)) {
					log.debug("throw CustomWebSocketException Code = {}",StompErrorCode.SESSION_DUPLICATION);
					throw new CustomWebSocketException(StompErrorCode.SESSION_DUPLICATION.name());
				}
				enteredChatUser.setSessionId(newSessionId);
			}
		}
		return message;
	}
	

}
