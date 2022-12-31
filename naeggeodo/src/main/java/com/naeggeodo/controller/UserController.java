package com.naeggeodo.controller;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.dto.NicknameDTO;
import com.naeggeodo.dto.QuickChatDTO;
import com.naeggeodo.repository.UserRepository;
import com.naeggeodo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;

	@PatchMapping(value="/user/{user_id}/address",produces = "application/json")
	public AddressDTO updateAddress(@PathVariable("user_id")String user_id
			,@RequestBody @Valid AddressDTO dto){
		return userService.updateUserAddress(user_id,dto);
	}

	@GetMapping(value="/user/{user_id}/address",produces = "application/json")
	public AddressDTO getAddress(@PathVariable("user_id")String user_id){
		return userService.findAddressById(user_id);
	}

	@GetMapping(value = "/user/{user_id}/mypage", produces = "application/json")
	public MypageDTO getMyPageData(@PathVariable("user_id") String user_id) {
		return userService.getMyPageData(user_id);
	}

	//해당 유저 퀵채팅
	@GetMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
	public QuickChatDTO getQuickChat(@PathVariable(name = "user_id") String user_id) {
		return userService.getQuickChat(user_id);
	}


	@PatchMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
	public QuickChatDTO updateQuickChat(@PathVariable("user_id")String user_id,
												  @RequestBody QuickChatDTO dto){
		return userService.updateQuickChat(user_id,dto);
	}

	@GetMapping(value = "/user/{user_id}/nickname",produces = "application/json")
	public NicknameDTO getNickname(@PathVariable(name="user_id")String user_id){
		return userService.getNickName(user_id);
	}

	@PatchMapping(value = "/user/{user_id}/nickname",produces = "application/json")
	public NicknameDTO
	updateNickname(@PathVariable(name="user_id")String user_id,
												 @RequestParam(name="value")String value){
		return userService.updateNickName(user_id, value);
	}
}
