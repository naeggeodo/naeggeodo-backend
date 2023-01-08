package com.naeggeodo.service;

import com.naeggeodo.dto.UserNameIdDTO;
import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatUserService {

    private final ChatUserRepository chatUserRepository;

    @Transactional(readOnly = true)
    public List<UserNameIdDTO> getChatUserList(Long chatMain_id) {
        return chatUserRepository.findForRemit(chatMain_id);
    }

    @Transactional(readOnly = true)
    public List<UserNameIdDTO> getBannedChatUserList(Long chatMain_id) {
        List<ChatUser> list = chatUserRepository.findByChatMainIdAndBanState(chatMain_id, BanState.BANNED);
        return list.stream()
                .map(UserNameIdDTO::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRemittanceState(Long chatMain_id, String user_id) {
        ChatUser chatUser = chatUserRepository.findByChatMainIdAndUserId(chatMain_id, user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        chatUser.changeState();
    }
}
