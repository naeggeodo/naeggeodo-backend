package com.naeggeodo.controller;


import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.dto.TargetMessageDTO;
import com.naeggeodo.exception.CustomWebSocketException;
import com.naeggeodo.exception.StompErrorCode;
import com.naeggeodo.handler.WebsocketSessionHandler;
import com.naeggeodo.service.StompService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {
    private final WebsocketSessionHandler sessionHandler;
    private final StompService stompService;
    private final SimpMessagingTemplate simpMessagingTemplate; // forTest


    //일반 메시지
    @MessageMapping("/chat/send")
    public void sendMsg(MessageDTO message, StompHeaderAccessor headers) {
        validateMessage(message, headers);
        stompService.send(message, headers);
    }

    //입장
    @MessageMapping("/chat/enter")
    public void enter(MessageDTO message, StompHeaderAccessor headers) throws Exception {
        validateMessage(message, headers);
        stompService.enter(message, headers);
    }

    //퇴장
    @MessageMapping(value = "/chat/exit")
    public void exit(MessageDTO message, StompHeaderAccessor headers) throws Exception {
        validateMessage(message, headers);
        stompService.exit(message, headers);
    }

    @MessageMapping("/chat/ban")
    public void ban(TargetMessageDTO message, StompHeaderAccessor headers) throws Exception {

        validateMessage(message, headers);

        String session_id = stompService.ban(message, headers);

        if (session_id != null)
            sessionHandler.close(session_id);
    }

    @MessageMapping("/chat/test")
    public void test(Message<?> message, StompHeaderAccessor stompHeaderAccessor) {
        stompHeaderAccessor.setNativeHeader("img", "img");
        System.out.println("message = " + message);
        simpMessagingTemplate.send("/topic/1", message);
    }

    //채팅종료!!
//	@Transactional
//	@MessageMapping("/chat/end")
//	public void endChat(MessageDTO message,StompHeaderAccessor headers){
//		validateMessage(message,headers);
//
//		ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(message.chatMain_idToLong());
//		List<ChatUser> chatUsers = chatMain.getChatUser();
//
//		sessionHandler.clear(chatUsers);
//		for (ChatUser cu : chatMain.getAllowedUserList()) {
//			dealRepository.save(Deal.create(cu.getUser(),chatMain));
//		}
//		chatMain.changeState(ChatState.END);
//		chatUserRepository.deleteAll(chatUsers);
//		chatUsers.clear();
//	}

    //quick-chat update
//	@Transactional
//	@MessageMapping("/chat/quick-chat/update")
//	public void updateQuickChat(MessageDTO message,StompHeaderAccessor headers) {
//		JSONObject json = new JSONObject(message.getContents());
//		JSONArray arr_json = new JSONArray(json.get("quickChat").toString());
//		String user_id = json.getString("user_id");
//
//		QuickChat quickChat = quickChatRepository.getByUserId(user_id);
//		quickChat.updateMsgByList(MyUtility.convertQuickChatJSONArrayToStringList(arr_json));
//		//chatMainService.updateQuickChat(arr_json, user_id);
//		JSONObject contents_json =  MyUtility.convertStringListToJSONObject(quickChat.getMsgList(), "quickChat");
//		contents_json.put("user_id", user_id);
//		message.setContents(contents_json.toString());
//		message.setType(ChatDetailType.SYSTEM);
//		sendToUser(headers.getSessionId(), message);
//	}

    private void validateMessage(MessageDTO dto, StompHeaderAccessor headers) {
        String chatMain_id = dto.getChatMain_id();
        String sender = dto.getSender();

        if (chatMain_id != null && sender != null) {
            chatMain_id = chatMain_id.trim();
            sender = sender.trim();
        }
        if (chatMain_id == null || chatMain_id.equals(""))
            throw new CustomWebSocketException(StompErrorCode.BAD_REQUEST.name(), headers);
        if (sender == null || sender.equals(""))
            throw new CustomWebSocketException(StompErrorCode.BAD_REQUEST.name(), headers);
    }

}
