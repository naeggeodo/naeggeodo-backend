package com.naeggeodo.controller;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.entity.chat.ChatDetailType;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.handler.SessionHandler;
import com.naeggeodo.service.ChatDetailService;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.service.ChatUserService;
import com.naeggeodo.util.MyUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {
	private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatDetailService chatDetailService;
    private final ChatMainService chatMainService;
    private final ChatUserService chatUserService;
    private final SessionHandler sessionHandler;
    
    
    //일반 메시지,이미지 보내기
    @MessageMapping("/chat/send")
    public void sendMsg(MessageDTO message) throws Exception {
        Long chatMain_id = message.getChatMain_id();
        if(message.getType().equals(ChatDetailType.TEXT)) {
        	//메시지 insert
        	chatDetailService.save(message);
        	//메시지 전송
        	sendToAll(chatMain_id, message);
        	return;
        } 
        
        if(message.getType().equals(ChatDetailType.IMAGE)) {
        	log.debug("file size = {} KB",MyUtility.getFileSizeInBase64StringWithKB(message.getContents()));
        	// 10MB 이상
        		sendToAll(chatMain_id, message);
        }
    }
    //입장
    @MessageMapping("/chat/enter")
    public void enter(MessageDTO message,StompHeaderAccessor headers) throws Exception {
    	 Long chatMain_id = message.getChatMain_id();
    	 String session_id = headers.getSessionId();
    	 //신규입장일때(chatUser 테이블에 접속한 사용자가 존재하지 않을때)
    	 if(!chatUserService.isExist(message)) {
    		//chatUser,입장메시지 insert, 
     		chatUserService.save(message,session_id);
     		chatDetailService.save(message);
     		//입장 메시지 전송
     		sendToAll(chatMain_id, message);
     	}
    	 //인원수 메시지 전송
    	 sendToAll(chatMain_id, getCountMessage(chatMain_id));
    	 // 채팅방 상태변경
    	 chatMainService.changeState(chatMain_id);
    }
    
    //퇴장
    @MessageMapping("/chat/exit")
    public void exit(MessageDTO message) throws Exception {
    	Long chatMain_id = message.getChatMain_id();
    	String sender = message.getSender();
    	
    	//메시지를 보낸 사람이 방장이 아닐때 
    	if(!chatMainService.isHost(chatMain_id, sender)) {
    		//나가기(delete), 퇴장메시지 db에 저장
    		chatUserService.exit(message);
        	chatDetailService.save(message);
        	//퇴장 메시지 전송
        	sendToAll(chatMain_id, message);
        	//인원수 메시지 전송
        	sendToAll(chatMain_id, getCountMessage(chatMain_id));
    	} else {
    		//일단 방장은 못나가게 해놨음 수정해야함
    		sendToUser(chatMain_id, sender, getAlertMessage("방장은 나갈수 없습니다."));
    	}
    	
    	chatMainService.changeState(chatMain_id);
    }
    
    //강퇴 (if문 정리 필요합니다)
    @MessageMapping("/chat/ban")
    public void ban(MessageDTO message) throws Exception {
    	//강퇴할 사람 sessionID 가져오기
    	String session_id = chatUserService.getSession_id(message);
    	Long chatMain_id = message.getChatMain_id();
    	String bannedUser = message.getContents();
    	String sender = message.getSender();
    	
    	// 보낸사람이 방장이고 자기자신이 아니라면
    	if(chatMainService.isHost(chatMain_id, sender)&& !bannedUser.equals(sender)) {
    		//강퇴 당한 유저에게 ALERT
    		sendToUser(chatMain_id, bannedUser, getAlertMessage("강퇴 당하셨습니다."));
    		//세션지우기
    		sessionHandler.ban(session_id);
    		//유저 delete
        	chatUserService.exit(chatMain_id,bannedUser);
        	
        	//강퇴 메시지 저장
        	message.setContents(bannedUser+"님이 강퇴 당하셨습니다.");
        	chatDetailService.save(message);
        	
        	//강퇴 메시지 전송 및 인원수 전송
        	sendToAll(chatMain_id, message);
        	sendToAll(chatMain_id, getCountMessage(chatMain_id));
    	} else if(!chatMainService.isHost(chatMain_id, sender)) {
    		// 방장이 아닐때
    		sendToUser(chatMain_id, sender, getAlertMessage("방장만 강퇴할 수 있습니다."));
    	} else if(bannedUser.equals(sender)){
    		//자기자신일떄
    		sendToUser(chatMain_id, sender, getAlertMessage("자기 자신은 강퇴 할 수 없습니다."));
    	} 
    	
    	chatMainService.changeState(chatMain_id);
    }
    //quick-chat update
    @MessageMapping("/chat/quick-chat/update")
    public void updateQuickChat(MessageDTO message) {
    	JSONObject json = new JSONObject(message.getContents());
    	JSONArray arr_json = new JSONArray(json.get("quickChat").toString());
    	String user_id = json.getString("user_id");
    	
    	chatMainService.updateQuickChat(arr_json, user_id);
    	JSONObject contents_json =  MyUtility.convertStringListToJSONObject(chatMainService.getQuickChat(user_id), "quickChat");
    	contents_json.put("user_id", user_id);
    	message.setContents(contents_json.toString());
    	message.setType(ChatDetailType.SYSTEM);
    	sendToUser(message.getChatMain_id(), user_id, message);
    }
    
    // 인원수 메시지 get
    private MessageDTO getCountMessage(Long chatMain_id) throws Exception {
    	int currentCount = chatMainService.getCurrentCount(chatMain_id);
    	List<ChatUser> list = chatUserService.currentList(chatMain_id);
    	
    	JSONObject json = new JSONObject();
    	json = MyUtility.convertListToJSONobj(list, "user");
    	json.put("currentCount", currentCount);
    	
    	MessageDTO messageDto = new MessageDTO();
    	messageDto.setChatMain_id(chatMain_id);
    	messageDto.setContents(json.toString());
    	messageDto.setType(ChatDetailType.CNT);
    	return messageDto;
    }
    
    // alert 메시지 get
    private MessageDTO getAlertMessage(String contents) {
    	MessageDTO message = new MessageDTO();
    	message.setContents(contents);
    	message.setType(ChatDetailType.ALERT);
    	return message;
    }
    
    // 전체 send
    private void sendToAll(Long chatMain_id,MessageDTO dto) {
    	simpMessagingTemplate.convertAndSend("/topic/"+chatMain_id,dto);
    }
    
    // 개인 send
    // https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session
    private void sendToUser(Long chatMain_id,String receiver,MessageDTO dto) {
    	String sessionId = chatUserService.getSession_id(chatMain_id,receiver);
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headers.setSessionId(sessionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+chatMain_id, dto,  headers.getMessageHeaders());
    }
    //개인 send 오버로딩 (deprecated)
    @Deprecated
    private void sendToUser(Long chatMain_id,String receiver,String message) {
    	String sessionId = chatUserService.getSession_id(chatMain_id,receiver);
    	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
    	headers.setSessionId(sessionId);
    	simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+chatMain_id, message,  headers.getMessageHeaders());
    }
    
    
}
