package com.naeggeodo.service;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.dto.TargetMessageDTO;
import com.naeggeodo.entity.chat.*;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatDetailRepository;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.repository.UserRepository;
import com.naeggeodo.util.MyUtility;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StompService {
    private final ChatMainRepository chatMainRepository;
    private final UserRepository userRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatDetailRepository chatDetailRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void send(MessageDTO message,StompHeaderAccessor headers){
        Users user = userRepository.getById(message.getSender());
        ChatMain chatMain = chatMainRepository.getById(message.chatMain_idToLong());
        chatDetailRepository.save(ChatDetail.create(message.getContents(),user,chatMain,ChatDetailType.TEXT));
        sendToAll(chatMain.getId(),message);
    }


    @Transactional
    public void enter(MessageDTO message, StompHeaderAccessor headers) throws Exception{
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
            chatDetailRepository.save(ChatDetail.create(message.getContents(),user,chatMain,message.getType()));
            chatMain.updateState();
            sendToAll(chatMain_id, message);
        }
        sendToAll(chatMain_id, getCountMessage(chatMain));
    }

    @Transactional
    public void exit(MessageDTO message,StompHeaderAccessor headers) throws Exception{
        Long chatMain_id = message.chatMain_idToLong();
        String sender = message.getSender();

        ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
        ChatUser exitChatUser = chatMain.findChatUserBySender(sender);
        Users user = userRepository.getById(sender);
        ChatUser nextHost = null;
        //메시지를 보낸 사람이 방장일때
        if(chatMain.getUser().getId().equals(sender)) {
            //다음 방장 get
            nextHost = chatMain.findChatUserForChangeHost();
            //다음방장이 null이라면 -> 혼자있으면
            if(nextHost ==null) {
                chatMain.changeState(ChatState.INCOMPLETE);
                chatUserRepository.delete(exitChatUser);
                chatMain.removeChatUser(exitChatUser);
                return;
            }
            chatMain.changeUser(nextHost.getUser());

        }
        //방장이 나갔을때
        if(nextHost != null) {
            //퇴장 메시지 전송
            message.setContents("방장이 나가서 "+nextHost.getUser().getNickname()+"님이 새로운 방장이 됩니다.");
        }
        sendToAll(chatMain_id, message);

        chatUserRepository.delete(exitChatUser);
        chatMain.removeChatUser(exitChatUser);
        chatDetailRepository.save(ChatDetail.create(message.getContents(),user,chatMain,ChatDetailType.EXIT));

        //인원수 메시지 전송
        sendToAll(chatMain_id, getCountMessage(chatMain));
        chatMain.updateState();
    }

    @Transactional
    public String ban(TargetMessageDTO message,StompHeaderAccessor headers) throws Exception{
        String target_id = message.getTarget_id();
        String sender = message.getSender();
        ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(message.chatMain_idToLong());
        ChatUser targetChatUser = chatMain.findChatUserBySender(target_id);
        String senderSessionId = headers.getSessionId();
        Users user = userRepository.getById(sender);

        if(!chatMain.getUser().getId().equals(sender)||target_id.equals(sender)) {
            // 보낸 사람이 방장이 아니거나 자기자신일때
            sendToUser(senderSessionId , getAlertMessage("bad request"));
            return null;
        }

        sendToUser(targetChatUser.getSessionId(),message);
        targetChatUser.setBanState(BanState.BANNED);
        chatMain.getChatUser().remove(targetChatUser);
        message.setContents("님이 강퇴 당하셨습니다.");
        chatDetailRepository.save(ChatDetail.create(message.getContents(),user,chatMain,ChatDetailType.BAN));

        sendToAll(chatMain.getId(), message);
        sendToAll(chatMain.getId(), getCountMessage(chatMain));

        chatMain.updateState();
        return targetChatUser.getSessionId();
    }





    private void sendToAll(Long chatMain_id,MessageDTO dto) {
        simpMessagingTemplate.convertAndSend("/topic/"+chatMain_id,dto);
    }
    private void sendToAll(Long chatMain_id, TargetMessageDTO dto) {
        simpMessagingTemplate.convertAndSend("/topic/"+chatMain_id,dto);
    }

    // 개인 send
    // https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session
    private void sendToUser(String sessionId,MessageDTO dto) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.MESSAGE);
        headers.setSessionId(sessionId);
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/"+sessionId, dto,  headers.getMessageHeaders());
    }

    private MessageDTO getCountMessage(ChatMain chatMain) throws Exception {
        List<ChatUser> allowedUserList = chatMain.getAllowedUserList();

        JSONObject json = MyUtility.convertListToJSONobj(allowedUserList, "users");
        json.put("currentCount", chatMain.getAllowedUserCnt());

        MessageDTO messageDto = new MessageDTO();
        messageDto.setChatMain_id(String.valueOf(chatMain.getId()));
        messageDto.setContents(json.toString());
        messageDto.setType(ChatDetailType.CNT);
        return messageDto;
    }

    private MessageDTO getAlertMessage(String contents) {
        MessageDTO message = new MessageDTO();
        message.setContents(contents);
        message.setType(ChatDetailType.ALERT);
        return message;
    }
}
