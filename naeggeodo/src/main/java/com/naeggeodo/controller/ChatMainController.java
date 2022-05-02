package com.naeggeodo.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
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
		
		JSONObject json = new JSONObject();
		
		if(category==null) {
			//전체조회시
			json.put("chat-room",chatMainService.getChatList(buildingCode));
		} else {
			//카테고리 조회시
			json.put("chat-room",chatMainService.getChatList(category,buildingCode));
		}
		
		return json.toJSONString();
		
	}
	
	//카테고리 리스트
	@GetMapping(value="/categories",produces = "application/json")
	public String getCategoryList() {
		List<JSONObject> list = new ArrayList<>();
		
		for (int i = 0; i < Category.values().length; i++) {
			JSONObject json = new JSONObject();
			json.put("idx", i);
			json.put("category", Category.values()[i].name());
			
			list.add(json);
		}
		JSONObject json = new JSONObject();
		json.put("category", list);
		return json.toJSONString();
	}
	
	
	
	
	//채팅방 생성
	@PostMapping(value= "/chat/rooms",produces ="application/json" )
	public String createChatRoom(@RequestPart ChatRoomDTO chat,@RequestPart MultipartFile file) {
		
		JSONObject json = chatMainService.createChatRoom(chat, file);
		return json.toJSONString();
	}
	
	
	//채팅방 상태 업데이트
	@PatchMapping(value="/chat/rooms/{chatMain_id}")
	@Transactional
	public String updateRoomState(@PathVariable(name="chatMain_id") Long chatMain_id,
		  						 @RequestParam(name="state") ChatState state) {
		chatMainRepository.updateState(chatMain_id, state);
		return "ok";
	}
	
	@Transactional
	@GetMapping(value="/chat/rooms/{chatMain_id}",produces ="application/json")
	public String chatRoom(@PathVariable(name="chatMain_id")Long chatMain_id) throws Exception {
		ChatMain chatMain = chatMainRepository.findOne(chatMain_id);
		return chatMain.toJSON().toJSONString();
	}
	
	@GetMapping(value="/chat/rooms/{chatMain_id}/users",produces = "application/json")
	public String getChatUserList(@PathVariable(name="chatMain_id")Long chatMain_id) {
		JSONObject json = new JSONObject();
		json.put("users", chatUserService.currentList(chatMain_id));
		return json.toJSONString();
	}
	
	@PatchMapping(value="/chat/rooms/{chatMain_id}/users/{user_id}",produces = "application/json")
	public String updateRemittanceState(@PathVariable(name="chatMain_id")Long chatMain_id,
										@PathVariable(name="user_id")Long user_id) {
		chatUserService.updateRemittanceStateToY(chatMain_id, user_id);
		return "success";
	}
	
}
