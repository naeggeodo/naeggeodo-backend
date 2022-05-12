package com.naeggeodo.controller;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.entity.chat.ChatDetailType;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.handler.SessionHandler;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.UserRepository;
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
    private final SessionHandler sessionHandler;
    private final ChatMainRepository chatMainRepository;
    private final ChatUserRepository chatUserRepository;
    private final UserRepository userRepository;
    private final QuickChatRepository quickChatRepository;
    
    
    //일반 메시지,이미지 보내기
    @MessageMapping("/chat/send")
    public void sendMsg(MessageDTO message) throws Exception {
        Long chatMain_id = message.getChatMain_id();
    	//메시지 insert
    	chatDetailService.save(message);
    	//메시지 전송
    	sendToAll(chatMain_id, message);
        
        if(message.getType().equals(ChatDetailType.IMAGE)) {
        	log.debug("file size = {} KB",MyUtility.getFileSizeInBase64StringWithKB(message.getContents()));
        }
    }
    //입장
    @Transactional
    @MessageMapping("/chat/enter")
    public void enter(MessageDTO message,StompHeaderAccessor headers) throws Exception {
    	System.out.println("================enter===================");
    	 Long chatMain_id = message.getChatMain_id();
    	 String sender = message.getSender();
    	 String session_id = headers.getSessionId();
    	 ChatMain chatMain  = chatMainRepository.findChatMainEntityGraph(chatMain_id);
    	 Users user = userRepository.getById(sender); 
    	 ChatUser enteredChatUser = chatMain.findChatUserBySender(sender);
    	 //신규입장일때
    	 if(enteredChatUser==null) {
    		//chatUser,입장메시지 insert
    		chatUserRepository.save(ChatUser.create(user, chatMain, session_id));
     		chatDetailService.save(message);
     		//입장 메시지 전송
     		sendToAll(chatMain_id, message);
     	}
    	 //인원수 메시지 전송
    	 sendToAll(chatMain_id, getCountMessage(chatMain));
    	 // 채팅방 상태변경
    	 System.out.println("=========chatMain changestate==========");
    	 
    	 chatMain.updateState();
    }
    
    //퇴장
    @MessageMapping("/chat/exit")
    @Transactional
    public void exit(MessageDTO message) throws Exception {
    	System.out.println("====================exit==============================");
    	Long chatMain_id = message.getChatMain_id();
    	String sender = message.getSender();
    	
    	ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
    	
    	//메시지를 보낸 사람이 방장이 아닐때 
    	if(!chatMain.getUser().getId().equals(sender)) {
    		//나가기(delete), 퇴장메시지 db에 저장
    		ChatUser chatUser = chatMain.findChatUserBySender( sender);
    		chatUserRepository.delete(chatUser);
    		chatMain.removeChatUser(chatUser);
    		
        	chatDetailService.save(message);
        	//퇴장 메시지 전송
        	sendToAll(chatMain_id, message);
        	//인원수 메시지 전송
        	sendToAll(chatMain_id, getCountMessage(chatMain));
    	} else {
    		//일단 방장은 못나가게 해놨음 수정해야함
    		sendToUser(chatMain_id, sender, getAlertMessage("방장은 나갈수 없습니다."));
    	}
    	
		chatMain.updateState();
    	System.out.println("====================exit==============================");
    }
    
    //개선
    @Transactional
    @MessageMapping("/chat/ban")
    public void ban(MessageDTO message,StompHeaderAccessor headers) throws Exception {
    	//강퇴할 사람 sessionID 가져오기
    	String bannedUser = message.getContents();
    	String sender = message.getSender();
    	ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(message.getChatMain_id());
    	ChatUser targetChatUser = chatMain.findChatUserBySender(bannedUser);
    	String senderSessionId = headers.getSessionId();
    	
    	
    	if(!chatMain.getUser().getId().equals(sender)||bannedUser.equals(sender)) {
    		// 보낸 사람이 방장이 아니거나 자기자신일때
    		sendToUser(chatMain.getId(),senderSessionId , getAlertMessage("bad request"));
    		return;
    	}
    	
    	sessionHandler.close(targetChatUser.getSessionId());
    	chatUserRepository.delete(targetChatUser);
    	chatMain.removeChatUser(targetChatUser);
    	message.setContents(bannedUser+"님이 강퇴 당하셨습니다.");
    	chatDetailService.save(message);
    	
    	sendToAll(chatMain.getId(), message);
    	sendToAll(chatMain.getId(), getCountMessage(chatMain));
    	
    	chatMain.updateState();
    }
    
    //quick-chat update
    @Transactional
    @MessageMapping("/chat/quick-chat/update")
    public void updateQuickChat(MessageDTO message) {
    	JSONObject json = new JSONObject(message.getContents());
    	JSONArray arr_json = new JSONArray(json.get("quickChat").toString());
    	String user_id = json.getString("user_id");

    	QuickChat quickChat = quickChatRepository.getByUserId(user_id);
    	quickChat.updateMsgByList(MyUtility.convertQuickChatJSONArrayToStringList(arr_json));
    	//chatMainService.updateQuickChat(arr_json, user_id);
    	JSONObject contents_json =  MyUtility.convertStringListToJSONObject(quickChat.getMsgList(), "quickChat");
    	contents_json.put("user_id", user_id);
    	message.setContents(contents_json.toString());
    	message.setType(ChatDetailType.SYSTEM);
    	sendToUser(message.getChatMain_id(), user_id, message);
    }
    // 인원수 메시지 get 
    // 개선하기전!!!!
    @Transactional
    private MessageDTO getCountMessage(Long chatMain_id) throws Exception {
    	//int currentCount = chatMainService.getCurrentCount(chatMain_id);
    	ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
    	List<ChatUser> chatUser = chatMain.getChatUser();
    	
    	JSONObject json = new JSONObject();
    	json = MyUtility.convertListToJSONobj(chatUser, "user");
    	json.put("currentCount", chatUser.size());
    	
    	MessageDTO messageDto = new MessageDTO();
    	messageDto.setChatMain_id(chatMain_id);
    	messageDto.setContents(json.toString());
    	messageDto.setType(ChatDetailType.CNT);
    	return messageDto;
    }
    //개선후!!!
    private MessageDTO getCountMessage(ChatMain chatMain) throws Exception {
    	List<ChatUser> chatUser = chatMain.getChatUser();
    	
    	JSONObject json = new JSONObject();
    	json = MyUtility.convertListToJSONobj(chatUser, "user");
    	json.put("currentCount", chatUser.size());
    	
    	MessageDTO messageDto = new MessageDTO();
    	messageDto.setChatMain_id(chatMain.getId());
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
    private void sendToUser(Long chatMain_id,String sessionId,MessageDTO dto) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headers.setSessionId(sessionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+chatMain_id, dto,  headers.getMessageHeaders());
    }
    //개인 send 오버로딩 (deprecated)
    @Deprecated
    private void sendToUser(Long chatMain_id,String sessionId,String message) {
    	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
    	headers.setSessionId(sessionId);
    	simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+chatMain_id, message,  headers.getMessageHeaders());
    }
    
    
    //추가됨
    
    
}
