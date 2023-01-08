package com.naeggeodo.service;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.dto.NicknameDTO;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public AddressDTO updateUserAddress(String userId, AddressDTO dto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        user.updateAddress(dto.getAddress(), dto.getZonecode(), dto.getBuildingCode());
        dto.setUser_id(user.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public AddressDTO findAddressById(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));

        return AddressDTO.createFromUser(user);
    }

    @Transactional(readOnly = true)
    public MypageDTO getMyPageData(String user_id) {
        MypageDTO mypageDTO = userRepository.getMyPageData(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        return mypageDTO;
    }

    @Transactional(readOnly = true)
    public List<String> getQuickChat(String user_id) {
        Users user = userRepository.findQuickChatEntityGraph(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        List<String> msgList = user.getQuickChat().getMsgList();
        return msgList;
    }

    @Transactional
    public List<String> updateQuickChat(String user_id, List<String> messages) {
        Users user = userRepository.findQuickChatEntityGraph(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        QuickChat quickChat = user.getQuickChat();
        quickChat.updateMsgByList(messages);
        return messages;
    }

    @Transactional(readOnly = true)
    public NicknameDTO getNickName(String user_id) {
        Users user = userRepository.findById(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));

        return new NicknameDTO(user.getNickname(), user.getId());
    }

    @Transactional
    public NicknameDTO updateNickName(String user_id, String nickname) {
        Users user = userRepository.findById(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));

        user.updateNickname(nickname);
        return new NicknameDTO(user.getNickname(), user.getId());
    }
}
