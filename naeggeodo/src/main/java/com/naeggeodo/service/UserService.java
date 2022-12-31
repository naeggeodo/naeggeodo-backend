package com.naeggeodo.service;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
