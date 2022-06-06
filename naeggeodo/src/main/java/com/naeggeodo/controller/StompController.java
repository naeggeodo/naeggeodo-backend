package com.naeggeodo.controller;


import java.util.List;

import com.naeggeodo.dto.TargetMessageDTO;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.exception.StompErrorCode;
import com.naeggeodo.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatDetailType;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.handler.SessionHandler;
import com.naeggeodo.service.ChatDetailService;
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
	private final ReportRepository reportRepository;
    
    
    //일반 메시지,이미지 보내기
    @MessageMapping("/chat/send")
    public void sendMsg(MessageDTO message,StompHeaderAccessor headers) {

		validateMessage(message,headers);
        Long chatMain_id = message.chatMain_idToLong();
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
		validateMessage(message,headers);
    	System.out.println("================enter===================");
    	 Long chatMain_id = message.chatMain_idToLong();
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
    	 System.out.println("=========chatMain changeState==========");
    	 
    	 chatMain.updateState();
    }
    
    //퇴장
    @MessageMapping("/chat/exit")
    @Transactional
    public void exit(MessageDTO message,StompHeaderAccessor headers) throws Exception {
		validateMessage(message,headers);
    	System.out.println("====================exit==============================");
    	Long chatMain_id = message.chatMain_idToLong();
    	String sender = message.getSender();
    	
    	ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
    	ChatUser exitChatUser = chatMain.findChatUserBySender(sender);
    	ChatUser nextHost = null;
    	//메시지를 보낸 사람이 방장일때 
    	if(chatMain.getUser().getId().equals(sender)) {
    		//다음 방장 get
    		nextHost = chatMain.findChatUserForChangeHost();
    		//다음방장이 null이라면 -> 혼자있으면
    		if(nextHost ==null) {
    			chatMain.changeState(ChatState.END);
    			return;
    		}
    		chatMain.changeUser(nextHost.getUser());
        
    	}
    	//방장이 아닌사람이 나갔을때
    	if(nextHost == null) {
    		//퇴장 메시지 전송
        	sendToAll(chatMain_id, message);
    	} else {
    		message.setContents("방장이 나가서"+nextHost.getUser().getId()+"님이 새로운 방장이 됩니다.");
    		sendToAll(chatMain_id, message);
    	}
    	chatUserRepository.delete(exitChatUser);
		chatMain.removeChatUser(exitChatUser);
		chatDetailService.save(message);
    	
    	//인원수 메시지 전송
    	sendToAll(chatMain_id, getCountMessage(chatMain));
		chatMain.updateState();
    	System.out.println("====================exit==============================");
    }
    
    //개선
    @Transactional
    @MessageMapping("/chat/ban")
    public void ban(TargetMessageDTO message,StompHeaderAccessor headers) throws Exception {

		validateMessage(message,headers);

		//강퇴할 사람 sessionID 가져오기
    	String target_id = message.getTarget_id();
    	String sender = message.getSender();
    	ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(message.chatMain_idToLong());
    	ChatUser targetChatUser = chatMain.findChatUserBySender(target_id);
    	String senderSessionId = headers.getSessionId();
    	
    	
    	if(!chatMain.getUser().getId().equals(sender)||target_id.equals(sender)) {
    		// 보낸 사람이 방장이 아니거나 자기자신일때
    		sendToUser(senderSessionId , getAlertMessage("bad request"));
    		return;
    	}
    	
    	sessionHandler.close(targetChatUser.getSessionId());
    	targetChatUser.setBanState(BanState.BANNED);
    	message.setContents(target_id+"님이 강퇴 당하셨습니다.");
    	chatDetailService.save(message);
    	
    	sendToAll(chatMain.getId(), message);
    	sendToAll(chatMain.getId(), getCountMessage(chatMain));

    	chatMain.updateState();
    }

//	@Transactional
//	@MessageMapping("/chat/report")
//	public void report(TargetMessageDTO messageDTO,StompHeaderAccessor headers){
//
//		validateMessage(messageDTO,headers);
//
//		String sender_id = messageDTO.getSender();
//		String target_id = messageDTO.getTarget_id();
//		Users sender = null;
//		Users target = null;
//		if(userRepository.countForReport(sender_id,target_id)){
//			sender = userRepository.getById(sender_id);
//			target = userRepository.getById(target_id);
//		} else {
//			sendToUser(headers.getSessionId(),getAlertMessage("올바르지 않은 요청입니다."));
//		}
//
//
//		Report report = Report.create(sender,target,messageDTO.getContents());
//		reportRepository.save(report);
//	}
    
    //quick-chat update
    @Transactional
    @MessageMapping("/chat/quick-chat/update")
    public void updateQuickChat(MessageDTO message,StompHeaderAccessor headers) {
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
    	sendToUser(headers.getSessionId(), message);
    }
    // 인원수 메시지 get 
    // 개선하기전!!!!
    private MessageDTO getCountMessage(ChatMain chatMain) throws Exception {
    	List<ChatUser> chatUser = chatMain.getChatUser();
    	
		JSONObject json = MyUtility.convertListToJSONobj(chatUser, "user");
    	json.put("currentCount", chatMain.getAllowedUserCnt());
    	
    	MessageDTO messageDto = new MessageDTO();
    	messageDto.setChatMain_id(String.valueOf(chatMain.getId()));
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
    private void sendToAll(Long chatMain_id,TargetMessageDTO dto) {
    	simpMessagingTemplate.convertAndSend("/topic/"+chatMain_id,dto);
    }

    // 개인 send
    // https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session
    private void sendToUser(String sessionId,MessageDTO dto) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headers.setSessionId(sessionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+sessionId, dto,  headers.getMessageHeaders());
    }
    //개인 send 오버로딩 (deprecated)
    @Deprecated
    private void sendToUser(String sessionId, String message) {
    	StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
    	headers.setSessionId(sessionId);
    	simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+sessionId, message,  headers.getMessageHeaders());
    }

	private void validateMessage(MessageDTO dto,StompHeaderAccessor headers){
		String chatMain_id = dto.getChatMain_id();
		String sender = dto.getSender();

		if(chatMain_id!=null&&sender!=null){
			chatMain_id = chatMain_id.trim();
			sender  = sender.trim();
		}
		if(chatMain_id==null||chatMain_id.equals(""))
			throw new CustomWebSocketException(StompErrorCode.BAD_REQUEST.name(),headers);
		if(sender==null||sender.equals(""))
			throw new CustomWebSocketException(StompErrorCode.BAD_REQUEST.name(),headers);
	}

    @MessageExceptionHandler(CustomWebSocketException.class)
	public void handleMessageException(CustomWebSocketException e){
		if(e.getHeaders()!=null) sendToUser(e.getHeaders().getSessionId(),getAlertMessage(e.getMsg()));
	}
	@MessageExceptionHandler(NullPointerException.class)
	public void handleMessageException(NullPointerException e){}

}
