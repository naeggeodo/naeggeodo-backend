package com.naeggeodo.controller;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserRepository userRepository;
	
	@Transactional
	@PatchMapping(value="/user/{user_id}/address",produces = "application/json")
	public String updateAddress(@PathVariable("user_id")String user_id
								,@RequestBody @Valid AddressDTO dto) throws Exception {
		Users user = userRepository.findById(user_id).
					orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		user.updateAddress(dto.getAddress(), dto.getZonecode(), dto.getBuildingCode());
		return user.AddresstoJSON().toString();
	}

	@Transactional
	@GetMapping(value="/user/{user_id}/address",produces = "application/json")
	public String getAddress(@PathVariable("user_id")String user_id){
		AddressDTO dto = userRepository.findAddressDTOById(user_id);
		return dto.toJSON().toString();
	}
}
