package com.naeggeodo.service;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.dto.QuickChatDTO;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

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
    public QuickChatDTO getQuickChat(String user_id) {
        Users user = userRepository.findQuickChatEntityGraph(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        List<String> msgList = user.getQuickChat().getMsgList();
        return new QuickChatDTO(msgList);
    }

    @Transactional
    public QuickChatDTO updateQuickChat(String user_id, QuickChatDTO dto) {
        Users user = userRepository.findQuickChatEntityGraph(user_id)
                .orElseThrow(() -> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
        QuickChat quickChat = user.getQuickChat();
        quickChat.updateMsgByList(dto.getQuickChat());
        return dto;
    }
}
