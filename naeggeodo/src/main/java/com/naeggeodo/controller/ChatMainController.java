package com.naeggeodo.controller;

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
import com.naeggeodo.entity.chat.BanState;
import com.naeggeodo.entity.chat.Category;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatState;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.entity.chat.RemittanceState;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.TagRepository;
import com.naeggeodo.service.ChatMainService;
import com.naeggeodo.util.MyUtility;

@RestController
public class ChatMainController {

	@Autowired
	private ChatMainRepository chatMainRepository;
	@Autowired
	private ChatMainService chatMainService;
	@Autowired
	private ChatUserRepository chatUserRepository;
	@Autowired
	private QuickChatRepository quickChatRepository;
	@Autowired
	private TagRepository tagRepository;
	
	//채팅방 리스트
	@GetMapping(value="/chat/rooms",produces = "application/json")
	@Transactional(readOnly = true)
	public String getChatList(
			@RequestParam(name = "category",required = false)String category,
			@RequestParam(name= "buildingcode",required = true)String buildingCode) throws Exception {
		
		if(category==null) {
			//전체조회시
			return MyUtility.convertListToJSONobj(chatMainRepository.findByBuildingCode(buildingCode), "chat-room").toString();
		} else {
			//카테고리 조회시
			Category categoryEnum = Category.valueOf(category.toUpperCase());
			return MyUtility.convertListToJSONobj(chatMainRepository.findByCategoryAndBuildingCode(categoryEnum, buildingCode), "chat-room").toString();
		}
	}
	
	//채팅방 생성
	@PostMapping(value= "/chat/rooms",produces ="application/json" )
	public String createChatRoom(@RequestPart(name = "chat") ChatRoomDTO chat,@RequestPart MultipartFile file) {
		System.out.println(chat.getTag());
		JSONObject json = chatMainService.createChatRoom(chat, file);
		return json.toString();
	}
//	@PostMapping(value= "/chat/rooms",produces ="application/json" )
//	public String createChatRoom(@RequestPart(name = "chat") Map<String, Object> map,@RequestPart MultipartFile file) {
//		System.out.println(map.get("addr"));
//		System.out.println(map.get("category"));
//		System.out.println(map.get("tag"));
//		return "hi";
//		
//		//JSONObject json = chatMainService.createChatRoom(chat, file);
//		//return json.toString();
//	}
	
	//해당 채팅방 data
	@GetMapping(value="/chat/rooms/{chatMain_id}",produces = "application/json")
	@Transactional(readOnly = true)
	public String getChatMain(@PathVariable(name = "chatMain_id") String chatMain_idstr) throws Exception {
		Long chatMain_id = Long.parseLong(chatMain_idstr);
		ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
		return chatMain.toJSON().toString();
	}
	
	//채팅방 상태 업데이트
	@PatchMapping(value="/chat/rooms/{chatMain_id}")
	@Transactional
	public String updateRoomState(@PathVariable(name="chatMain_id") Long chatMain_id,
									@RequestParam(name="state")String state) {
		ChatState chatState = ChatState.valueOf(state.toUpperCase());
		ChatMain chatMain = chatMainRepository.findById(chatMain_id).get();
		chatMain.changeState(chatState);
		return chatMain.getState().name();
	}
	
	
	// 해당 채팅방접속중인 유저 list
	@GetMapping(value="/chat/rooms/{chatMain_id}/users",produces = "application/json")
	@Transactional(readOnly = true)
	public String getChatUserList(@PathVariable(name="chatMain_id")Long chatMain_id) throws Exception {
		return MyUtility.convertListToJSONobj(chatUserRepository.findByChatMainId(chatMain_id), "users").toString();
	}
	
	//송금상태 변경
	@PatchMapping(value="/chat/rooms/{chatMain_id}/users/{user_id}",produces = "application/json")
	@Transactional
	public String updateRemittanceState(@PathVariable(name="chatMain_id")Long chatMain_id,
										@PathVariable(name="user_id")String user_id) {
		ChatUser chatUser =  chatUserRepository.findByChatMainIdAndUserId(chatMain_id, user_id);
		chatUser.setState(RemittanceState.Y);
		return "success";
	}
	
	//해당 유저 퀵채팅
	@Transactional(readOnly = true)
	@GetMapping(value="/chat/user/{user_id}/quick-chatting",produces = "application/json")
	public String getQuickChat(@PathVariable(name ="user_id")String user_id) {
		
		List<String> list = quickChatRepository.findByUserId(user_id).getMsgList();
		JSONObject json = MyUtility.convertStringListToJSONObject(list, "quickChat");
		json.put("user_id", user_id);
		return json.toString();
	}
	
	//참여중인 채팅방
	@Transactional(readOnly = true)
	@GetMapping(value="/chat/rooms/progressing/user/{user_id}",produces = "application/json")
	public String getProgressingChatList(@PathVariable(name="user_id") String user_id) throws Exception {
		List<ChatMain> list = chatMainRepository.findByUserIdInChatUser(user_id);
		return MyUtility.convertListToJSONobj(list, "chat-room").toString();
	}
	
	//상태,방장아이디로 조회
	@Transactional(readOnly= true)
	@GetMapping(value="/chat/rooms/user/{user_id}",produces = "application/json")
	public String getChatListByStateAndUserId(@PathVariable(name="user_id")String user_id,@RequestParam(name="state") String state) throws Exception {
		
		ChatState chatState = ChatState.valueOf(state.toUpperCase());
		List<ChatMain> list = chatMainRepository.findByStateAndUserId(chatState, user_id);
		JSONObject json;
		if(ChatState.END.equals(chatState)) {
			json = MyUtility.convertListToJSONobjIgnoringCurrentCount(list, "chat-room");
		} else {
			json = MyUtility.convertListToJSONobj(list, "chat-room");
		}
		
		
		return json.toString();
	}
	
	
	
	//카테고리 리스트
	@GetMapping(value="/categories",produces = "application/json")
	public String getCategoryList() throws Exception {
		return MyUtility.convertCategoryToJSONobj("categories").toString();
	}
	
	@GetMapping(value = "/chat/rooms/tag/most-wanted",produces = "application/json")
	@Transactional(readOnly = true)
	public String getMostWantedTagList() throws Exception {
		List<String> list = tagRepository.findTop10Tag();
		return MyUtility.convertStringListToJSONObject(list, "tags").toString();
	}
	@GetMapping(value="/chat/rooms/tag/{tag}",produces = "application/json")
	@Transactional(readOnly = true)
	public String getChatListByTag(@PathVariable("tag")String tag) throws Exception {
		List<ChatMain> list = chatMainRepository.findByTagName(tag);
		return MyUtility.convertListToJSONobj(list, "chat-room").toString();
	}
	@GetMapping(value="/chat/rooms/search/{keyWord}",produces = "application/json")
	@Transactional(readOnly = true)
	public String getChatListByKeyWord(@PathVariable("keyWord")String keyWord) throws Exception {
		List<ChatMain> list = chatMainRepository.findByTagNameOrTitleContains(keyWord, keyWord);
		return MyUtility.convertListToJSONobj(list, "chat-room").toString();
	}
	
	@GetMapping(value="/chat/rooms/{chatMain_id}/banned-user",produces = "application/json")
	@Transactional(readOnly = true)
	public String getBannedChatUserList(@PathVariable("chatMain_id")String chatMain_idstr) throws Exception {
		Long chatMain_id = Long.parseLong(chatMain_idstr);
		List<ChatUser> list = chatUserRepository.findByChatMainIdAndBanState(chatMain_id, BanState.BANNED);
		return MyUtility.convertListToJSONobj(list, "users").toString();
	}
}
