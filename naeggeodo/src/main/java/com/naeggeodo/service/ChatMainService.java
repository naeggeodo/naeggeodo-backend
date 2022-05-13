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
import com.naeggeodo.entity.chat.Tag;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.TagRepository;
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
	private final TagRepository tagRepository;
	
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
		ChatMain chatMain = ChatMain.create(dto);
		List<String> TagStringList = dto.getTag();
		List<Tag> tagList = new ArrayList<>();
		ChatMain savedChatMain = chatMainRepository.save(chatMain);
		for (String name : TagStringList) {
			tagList.add(Tag.create(savedChatMain, name));
		}
		tagRepository.saveAll(tagList);
		//Long chatMain_id = chatMainRepository.save(chatmain).getId();
		String imgpath = cloudinaryService.upload(file, "chatMain/"+savedChatMain.getId());
		chatMain.updateImgPath(imgpath);
		
		JSONObject json = new JSONObject();
		json.put("chatMain_id", savedChatMain.getId());
		return json;
	}
	
	
	
	
	@Transactional
	public List<String> getQuickChat(String user_id) {
		Users user = userRepository.findById(user_id).get();
		return user.getQuickChat().getMsgList();
	}
//	@Transactional
//	public void updateQuickChat(JSONArray arr_json,String user_id) {
//		QuickChat quickChat = quickChatRepository.getByUserId(user_id);
//		List<String> list = MyUtility.convertQuickChatJSONArrayToStringList(arr_json);
//		quickChat = QuickChat.updateMsgByList(list);
//	}
	
	@Transactional
	public List<ChatMain> getProgressingChatList(String user_id){
		return chatMainRepository.findByUserIdInChatUser(user_id);
	}
	
	
}
