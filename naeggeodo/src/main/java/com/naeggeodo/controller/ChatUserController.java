package com.naeggeodo.controller;

import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.entity.chat.RemittanceState;
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.util.MyUtility;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserRepository chatUserRepository;

    // 해당 채팅방접속중인 유저 list
    @GetMapping(value = "/chat-rooms/{chatMain_id}/users", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getChatUserList(@PathVariable(name = "chatMain_id") Long chatMain_id) throws Exception {
        JSONObject json = MyUtility.convertListToJSONobj(chatUserRepository.findForRemit(chatMain_id), "users");

        return ResponseEntity.ok(json.toMap());
    }

    //송금상태 변경
    @PatchMapping(value = "/chat-rooms/{chatMain_id}/users/{user_id}", produces = "application/json")
    @Transactional
    public ResponseEntity<Object> updateRemittanceState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                        @PathVariable(name = "user_id") String user_id) {
        ChatUser chatUser = chatUserRepository.findByChatMainIdAndUserId(chatMain_id, user_id);
        chatUser.setState(RemittanceState.Y.equals(chatUser.getState()) ? RemittanceState.N : RemittanceState.Y);
        return ResponseEntity.ok(chatUser.getState());
    }

    //강퇴 유저 리스트
    @GetMapping(value = "/chat-rooms/{chatMain_id}/banned-user", produces = "application/json")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getBannedChatUserList(@PathVariable("chatMain_id") String chatMain_idstr) throws Exception {
        Long chatMain_id = Long.parseLong(chatMain_idstr);
        List<ChatUser> list = chatUserRepository.findByChatMainIdAndBanState(chatMain_id, BanState.BANNED);
        JSONObject json = MyUtility.convertListToJSONobj(list, "users");
        return ResponseEntity.ok(json.toMap());
    }
}
