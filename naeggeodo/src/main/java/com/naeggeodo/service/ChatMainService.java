package com.naeggeodo.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.UserRepository;
import com.naeggeodo.util.MyUtility;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ChatMainService {
	
	private final ChatMainRepository chatMainRepository;
	private final CloudinaryService cloudinaryService;
	private final UserRepository userRepository;
	private final QuickChatRepository quickChatRepository;
	
	//파라미터 2개일때 param[0] -> category , param[1] -> BuildingCode
	@Transactional
	public List<ChatMain> getChatList(String... param) throws Exception {
		
		List<ChatMain> list = null;
		
		if(param.length == 1) {
			list = chatMainRepository.findByBuildingCode(param[0]);
		} else if(param.length == 2){
			param[0] = param[0].toUpperCase();
			Category category = Category.valueOf(param[0]);
			list = chatMainRepository.findBycategoryAndBuildingCode(category, param[1]);
		}
		return list;
	}
	
	//채팅방 생성
	@Transactional
	public JSONObject createChatRoom(ChatRoomDTO dto,MultipartFile file) {
		ChatMain chatmain = ChatMain.create(dto);

		Long chatMain_id = chatMainRepository.save(chatmain);
		String imgpath = cloudinaryService.upload(file, "chatMain/"+chatMain_id);
		chatmain.setImgPath(imgpath);
		
		JSONObject json = new JSONObject();
		json.put("chatMain_id", chatMain_id);
		return json;
	}
	
	//현재 인원수 get
	@Transactional
	public int getCurrentCount(Long chatMain_id) {
		return chatMainRepository.findOne(chatMain_id).getChatUser().size();
	}
	
	
	
	//꽉찼냐?
	@Transactional
	public boolean isFull(Long id) {
		ChatMain chatMain = chatMainRepository.findOne(id);
		int maxCount = chatMain.getMaxCount();
		int currentCount = chatMain.getChatUser().size();
		
		if(currentCount>=maxCount) {
			return true;
		}
		return false;
	}
	
	//방장이냐?
	@Transactional
	public boolean isHost(Long chatMain_id,String user_id) {
		ChatMain chatMain = chatMainRepository.findOne(chatMain_id);
		
		if(chatMain.getUser().getId().equals(user_id)) {
			return true;
		}
		
		return false;
	}
	
	@Transactional
	public void changeState(Long chatMain_id) {
		ChatMain chatMain = chatMainRepository.findOne(chatMain_id);
		
		if(chatMain.getChatUser().size() >= chatMain.getMaxCount()) {
			chatMainRepository.updateState(chatMain_id, ChatState.FULL);
		} else {
			chatMainRepository.updateState(chatMain_id, ChatState.CREATE);
		}
	}
	
	@Transactional
	public List<String> getQuickChat(String user_id) {
		Users user = userRepository.findOne(user_id);
		return user.getQuickChat().getMsgList();
	}
	//이상해!
	@Transactional
	public void updateQuickChat(JSONArray arr_json,String user_id) {
		QuickChat quickChat = quickChatRepository.findByUserId(user_id);
		quickChat.setMsg1(new JSONObject(arr_json.get(0).toString()).getString("msg"));
		quickChat.setMsg2(new JSONObject(arr_json.get(1).toString()).getString("msg"));
		quickChat.setMsg3(new JSONObject(arr_json.get(2).toString()).getString("msg"));
		quickChat.setMsg4(new JSONObject(arr_json.get(3).toString()).getString("msg"));
		quickChat.setMsg5(new JSONObject(arr_json.get(4).toString()).getString("msg"));
	}
}
