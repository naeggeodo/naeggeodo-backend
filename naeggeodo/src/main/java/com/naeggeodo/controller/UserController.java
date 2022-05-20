package com.naeggeodo.controller;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserRepository userRepository;
	
	@Transactional
	@PatchMapping(value="/user/{user_id}/address",produces = "application/json")
	public String updateAddress(@PathVariable("user_id")String user_id
								,@RequestBody AddressDTO dto) throws Exception {
		Users user = userRepository.findById(user_id).
					orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		user.updateAddress(dto.getAddress(), dto.getZonecode(), dto.getBuildingCode());
		return user.AddresstoJSON().toString();
	}
	
}
