package com.naeggeodo.config;


import com.naeggeodo.handler.MyStompSubProtocolErrorHandler;
import com.naeggeodo.handler.MySubProtocolHandler;
import com.naeggeodo.handler.StompInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfig extends WebSocketMessageBrokerConfigurationSupport implements WebSocketMessageBrokerConfigurer{
	
	private final StompInterceptor stompInterceptor;
	private final MyStompSubProtocolErrorHandler myStompSubProtocolErrorHandler;
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//subscribe url
		registry.enableSimpleBroker("/topic","/queue");
		//publish url
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompInterceptor);
		super.configureClientInboundChannel(registration);
	}
	
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		super.configureClientOutboundChannel(registration);
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//SockJs 생성 url
		registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
		registry.setErrorHandler(myStompSubProtocolErrorHandler);
	}
	
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(50*1024*1024);
		registry.setSendBufferSizeLimit(50*1024*1024);
		registry.setSendTimeLimit(20 * 10000);
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
	}

	

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		super.addReturnValueHandlers(returnValueHandlers);
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		return super.configureMessageConverters(messageConverters);
	}
	
	@Override
	public WebSocketHandler subProtocolWebSocketHandler(AbstractSubscribableChannel clientInboundChannel,
			AbstractSubscribableChannel clientOutboundChannel) {
		return new MySubProtocolHandler(clientInboundChannel, clientOutboundChannel);
	}

	
	

}
