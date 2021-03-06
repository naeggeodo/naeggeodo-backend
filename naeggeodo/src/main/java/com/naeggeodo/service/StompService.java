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
        //??????????????????
        if(enteredChatUser==null) {
            //chatUser,??????????????? insert
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
        //???????????? ?????? ????????? ????????????
        if(chatMain.getUser().getId().equals(sender)) {
            //?????? ?????? get
            nextHost = chatMain.findChatUserForChangeHost();
            //??????????????? null????????? -> ???????????????
            if(nextHost ==null) {
                chatMain.changeState(ChatState.INCOMPLETE);
                chatUserRepository.delete(exitChatUser);
                chatMain.removeChatUser(exitChatUser);
                return;
            }
            chatMain.changeUser(nextHost.getUser());

        }
        //????????? ????????????
        if(nextHost != null) {
            //?????? ????????? ??????
            message.setContents("????????? ????????? "+nextHost.getUser().getNickname()+"?????? ????????? ????????? ?????????.");
        }
        sendToAll(chatMain_id, message);

        chatUserRepository.delete(exitChatUser);
        chatMain.removeChatUser(exitChatUser);
        chatDetailRepository.save(ChatDetail.create(message.getContents(),user,chatMain,ChatDetailType.EXIT));

        //????????? ????????? ??????
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
            // ?????? ????????? ????????? ???????????? ??????????????????
            sendToUser(senderSessionId , getAlertMessage("bad request"));
            return null;
        }

        sendToUser(targetChatUser.getSessionId(),message);
        targetChatUser.setBanState(BanState.BANNED);
        chatMain.getChatUser().remove(targetChatUser);
        message.setContents("?????? ?????? ??????????????????.");
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

    // ?????? send
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
