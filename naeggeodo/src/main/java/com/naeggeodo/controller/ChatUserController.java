package com.naeggeodo.controller;

import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.dto.UserNameIdDTO;
import com.naeggeodo.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.naeggeodo.util.ResponseUtils.success;

@RestController
@RequiredArgsConstructor
public class ChatUserController {
    private final ChatUserService chatUserService;

    // 해당 채팅방접속중인 유저 list
    @GetMapping(value = "/chat-rooms/{chatMain_id}/users", produces = "application/json")

    public ResponseDTO<List<UserNameIdDTO>> getChatUserList(@PathVariable(name = "chatMain_id") Long chatMain_id) {
        List<UserNameIdDTO> list = chatUserService.getChatUserList(chatMain_id);
        return success(list);
    }

    //송금상태 변경
    @PatchMapping(value = "/chat-rooms/{chatMain_id}/users/{user_id}", produces = "application/json")
    public ResponseDTO<Object> updateRemittanceState(@PathVariable(name = "chatMain_id") Long chatMain_id,
                                                     @PathVariable(name = "user_id") String user_id) {
        chatUserService.updateRemittanceState(chatMain_id, user_id);
        return success(null);
    }

    //강퇴 유저 리스트
    @GetMapping(value = "/chat-rooms/{chatMain_id}/banned-user", produces = "application/json")
    public ResponseDTO<List<UserNameIdDTO>> getBannedChatUserList(@PathVariable("chatMain_id") Long chatMain_id) {
        List<UserNameIdDTO> list = chatUserService.getBannedChatUserList(chatMain_id);
        return success(list);
    }
}
