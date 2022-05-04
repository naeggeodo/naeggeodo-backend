package com.naeggeodo.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.service.ChatUserService;
import com.naeggeodo.util.MyUtility;

@RestController
public class ChatMainController {

	@Autowired
	private ChatMainRepository chatMainRepository;
	@Autowired
	private ChatMainService chatMainService;
	@Autowired
	private ChatUserService chatUserService;
	
	
	
	//채팅방 리스트

	@GetMapping(value="/chat/rooms",produces = "application/json")
	public String getChatList(
			@RequestParam(name = "category",required = false)String category,
			@RequestParam(name= "buildingcode",required = true)String buildingCode) throws Exception {
		
		if(category==null) {
			//전체조회시
			return MyUtility.convertListToJSONobj(chatMainService.getChatList(buildingCode), "chat-room").toString();
		} else {
			//카테고리 조회시
			return MyUtility.convertListToJSONobj(chatMainService.getChatList(category,buildingCode), "chat-room").toString();
		}
	}
	
	//카테고리 리스트
	@GetMapping(value="/categories",produces = "application/json")
	public String getCategoryList() throws Exception {
		return MyUtility.convertCategoryToJSONobj("categories").toString();
	}
	
	
	//채팅방 생성
	@PostMapping(value= "/chat/rooms",produces ="application/json" )
	public String createChatRoom(@RequestPart ChatRoomDTO chat,@RequestPart MultipartFile file) {
		
		JSONObject json = chatMainService.createChatRoom(chat, file);
		return json.toString();
	}
	
	@GetMapping(value="/chat/rooms/{chatMain_id}",produces = "application/json")
	@Transactional
	public String getChatMain(@PathVariable(name = "chatMain_id") String chatMain_idstr) throws Exception {
		Long chatMain_id = Long.parseLong(chatMain_idstr);
		ChatMain chatMain = chatMainRepository.findOne(chatMain_id);
		return chatMain.toJSON().toString();
	}
	
	//채팅방 상태 업데이트
	@PatchMapping(value="/chat/rooms/{chatMain_id}")
	@Transactional
	public String updateRoomState(@PathVariable(name="chatMain_id") Long chatMain_id,
		  						 @RequestParam(name="state") ChatState state) {
		chatMainRepository.updateState(chatMain_id, state);
		return "ok";
	}
	
	
	@GetMapping(value="/chat/rooms/{chatMain_id}/users",produces = "application/json")
	public String getChatUserList(@PathVariable(name="chatMain_id")Long chatMain_id) throws Exception {
		return MyUtility.convertListToJSONobj(chatUserService.currentList(chatMain_id), "users").toString();
	}
	
	@PatchMapping(value="/chat/rooms/{chatMain_id}/users/{user_id}",produces = "application/json")
	public String updateRemittanceState(@PathVariable(name="chatMain_id")Long chatMain_id,
										@PathVariable(name="user_id")String user_id) {
		chatUserService.updateRemittanceStateToY(chatMain_id, user_id);
		return "success";
	}
	
	@GetMapping(value="/chat/user/{user_id}/quick-chatting",produces = "application/json")
	public String getQuickChat(@PathVariable(name ="user_id")String user_id) {
		List<String> list = chatMainService.getQuickChat(user_id);
		JSONObject json = MyUtility.convertStringListToJSONObject(list, "quickChat");
		json.put("user_id", user_id);
		return json.toString();
	}
	
}
