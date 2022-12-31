package com.naeggeodo.controller;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.dto.QuickChatDTO;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.repository.UserRepository;
import com.naeggeodo.service.UserService;
import com.naeggeodo.util.MyUtility;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//	@Transactional
//	@PatchMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
//	public ResponseEntity<Object> updateQuickChat(@PathVariable("user_id")String user_id,
//												  @RequestBody Map<String,List<String>> quickChat){
//		QuickChat findQuickChat = userRepository.findQuickChatEntityGraph(user_id)
//				.orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND)).getQuickChat();
//		List<String> list = quickChat.get("quickChat");
//		findQuickChat.updateMsgByList(list);
//		JSONObject json = MyUtility.convertStringListToJSONObject(list,"quickChat");
//		return ResponseEntity.ok(json.toMap());
//	}

	@PatchMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
	public QuickChatDTO updateQuickChat(@PathVariable("user_id")String user_id,
												  @RequestBody QuickChatDTO dto){
		return userService.updateQuickChat(user_id,dto);
	}


	@Transactional(readOnly = true)
	@GetMapping(value = "/user/{user_id}/nickname",produces = "application/json")
	public ResponseEntity<Object> getNickname(@PathVariable(name="user_id")String user_id){
		Users user = userRepository.findById(user_id)
				.orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		JSONObject json = new JSONObject();
		json.put("nickname",user.getNickname());
		json.put("user_id",user.getId());
		return ResponseEntity.ok(json.toMap());
	}

	@Transactional
	@PatchMapping(value = "/user/{user_id}/nickname",produces = "application/json")
	public ResponseEntity<Object> updateNickname(@PathVariable(name="user_id")String user_id,
												 @RequestParam(name="value")String value){
		Users user = userRepository.findById(user_id)
				.orElseThrow(()->new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		user.setNickname(value);
		return ResponseEntity.ok(new HashMap<String,String>(){
				{
					put("user_id",user.getId());
					put("nickname",user.getNickname());
				}
			}
		);
	}
}
