package com.naeggeodo.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionHandler {
	 private final Map<String, WebSocketSession> sessionMap = new HashMap<>();
	 
	 public SessionHandler() {;}
	 
	 public void register(WebSocketSession session) {
		 sessionMap.put(session.getId(), session);
		 log.debug("접속중인세션 {}", sessionMap.keySet());
	 }
	 
	 public void close(WebSocketSession session) {
		 try {
				sessionMap.get(session.getId()).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 sessionMap.remove(session.getId());
		 log.debug("접속중인세션 {}", sessionMap.keySet());
	 }
	 
	 public void ban(String session_id) {
		 if(sessionMap.get(session_id) != null) {
			 try {
				sessionMap.get(session_id).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		 }
		 sessionMap.remove(session_id);
	 }
	 
	 public boolean isExist(String session_id) {
		 if(sessionMap.get(session_id) != null) {
			 return true;
		 }
		 return false;
	 }
}
