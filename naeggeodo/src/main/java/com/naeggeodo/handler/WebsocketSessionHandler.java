package com.naeggeodo.handler;

import com.naeggeodo.entity.chat.ChatUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WebsocketSessionHandler implements StompSessionHandler {
    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    public WebsocketSessionHandler() {
    }

    public void register(WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    public void close(WebSocketSession session) {
        if (session != null) {
            try {
                sessionMap.get(session.getId()).close();
                sessionMap.remove(session.getId());
            } catch (IOException e) {
                log.warn("SessionHandler catch Exception = ", e.getCause());
            }
        }

    }

    public void close(String session_id) {
        if (sessionMap.get(session_id) != null) {
            try {
                sessionMap.get(session_id).close();
                sessionMap.remove(session_id);
            } catch (IOException e) {
                log.warn("SessionHandler catch Exception = ", e.getCause());
            }

        }

    }

    public void clear(List<ChatUser> chatUsers) {
        for (ChatUser cu : chatUsers) {
            close(cu.getSessionId());
        }
    }

    public boolean isExist(String session_id) {
        return sessionMap.get(session_id) != null;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return null;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

    }


}
