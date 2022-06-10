package com.naeggeodo.controller;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;
import java.util.List;

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
		Users user = userRepository.findById(user_id).
				orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		return user.AddresstoJSON().toString();
	}

	@Transactional
	@GetMapping(value = "/user/{user_id}/mypage",produces = "application/json")
	public String getMyPageDate(@PathVariable("user_id")String user_id){
		List<Object> list = userRepository.getMyPageCount(user_id);
		JSONObject json = new JSONObject();
		json.put("participatingChatCount",Integer.valueOf(String.valueOf(list.get(0))));
		json.put("myOrdersCount",Integer.valueOf(String.valueOf(list.get(1))));
		json.put("nickname",list.get(2));
		return json.toString();
	}
}
