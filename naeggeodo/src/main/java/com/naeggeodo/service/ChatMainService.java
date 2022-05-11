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
			list = chatMainRepository.findByCategoryAndBuildingCode(category, param[1]);
		}
		return list;
	}
	
	//채팅방 생성
	@Transactional
	public JSONObject createChatRoom(ChatRoomDTO dto,MultipartFile file) {
		ChatMain chatmain = ChatMain.create(dto);

		Long chatMain_id = chatMainRepository.save(chatmain).getId();
		String imgpath = cloudinaryService.upload(file, "chatMain/"+chatMain_id);
		chatmain.updateImgPath(imgpath);
		
		JSONObject json = new JSONObject();
		json.put("chatMain_id", chatMain_id);
		return json;
	}
	
	
	
	//현재 인원수 get
	@Transactional
	public int getCurrentCount(Long chatMain_id) {
		return chatMainRepository.findById(chatMain_id).get().getChatUser().size();
	}
	
	
	
	//꽉찼냐?
	@Transactional
	public boolean isFull(Long id) {
		ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(id);
		int maxCount = chatMain.getMaxCount();
		int currentCount = chatMain.getChatUser().size();
		
		if(currentCount>=maxCount) {
			return true;
		}
		return false;
	}
	
	public boolean isFull(ChatMain chatMain) {
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
		ChatMain chatMain = chatMainRepository.findById(chatMain_id).get();
		
		if(chatMain.getUser().getId().equals(user_id)) {
			return true;
		}
		return false;
	}
	
	@Transactional
	public void changeState(Long chatMain_id) {
		System.out.println("===========change()========");
		ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
		
		if(chatMain.getChatUser().size() >= chatMain.getMaxCount()) {
			chatMain.updateState(ChatState.FULL);
		} else {
			chatMain.updateState(ChatState.CREATE);
		}
		System.out.println("===========change()========");
	}
	
	
	@Transactional
	public List<String> getQuickChat(String user_id) {
		Users user = userRepository.findById(user_id).get();
		return user.getQuickChat().getMsgList();
	}
	@Transactional
	public void updateQuickChat(JSONArray arr_json,String user_id) {
		QuickChat quickChat = quickChatRepository.findByUserId(user_id);
		List<String> list = MyUtility.convertQuickChatJSONArrayToStringList(arr_json);
		quickChat = QuickChat.updateMsgByList(list);
	}
	
	@Transactional
	public List<ChatMain> getProgressingChatList(String user_id){
		return chatMainRepository.findByUserIdInChatUser(user_id);
	}
	
	@Transactional
	public ChatState getState(Long chatMain_id) {
		ChatMain chatMain = chatMainRepository.findById(chatMain_id).get();
		return chatMain.getState();
	}
	
}
