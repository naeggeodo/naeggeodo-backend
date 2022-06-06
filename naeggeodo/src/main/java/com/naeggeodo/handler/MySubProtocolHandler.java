package com.naeggeodo.handler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySubProtocolHandler extends SubProtocolWebSocketHandler{
	

	@Autowired
	private SessionHandler sessionHandler;
	
	public MySubProtocolHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
		super(clientInboundChannel, clientOutboundChannel);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void addProtocolHandler(SubProtocolHandler handler) {
		// TODO Auto-generated method stub
		super.addProtocolHandler(handler);
	}

	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.debug("session is opened = {}",session.getId());
		sessionHandler.register(session);
		super.afterConnectionEstablished(session);
	}


	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		log.debug("session is closed = {}",session.getId());
		sessionHandler.close(session);
		super.afterConnectionClosed(session, closeStatus);
	}
	
}
