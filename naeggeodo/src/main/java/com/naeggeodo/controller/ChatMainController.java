package com.naeggeodo.controller;

import java.util.List;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.xml.ws.Response;

@RestController
@RequiredArgsConstructor
public class ChatMainController {

	private final ChatMainRepository chatMainRepository;
	private final ChatMainService chatMainService;
	private final ChatUserRepository chatUserRepository;
	private final QuickChatRepository quickChatRepository;
	private final TagRepository tagRepository;
	
	//채팅방 리스트
	@GetMapping(value="/chat-rooms",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getChatList(
			@RequestParam(name = "category",required = false)String category,
			@RequestParam(name= "buildingCode",required = true)String buildingCode) throws Exception {
		if(category==null) {
			//전체조회시
			JSONObject json = MyUtility.convertListToJSONobj(chatMainRepository.findByBuildingCode(buildingCode), "chat-room");
			return ResponseEntity.ok(json.toMap());
			//return MyUtility.convertListToJSONobj(chatMainRepository.findByBuildingCode(buildingCode), "chat-room").toString();
		} else {
			//카테고리 조회시
			Category categoryEnum = Category.valueOf(category.toUpperCase());
			JSONObject json = MyUtility.convertListToJSONobj(chatMainRepository.findByCategoryAndBuildingCode(categoryEnum, buildingCode), "chat-room");
			return ResponseEntity.ok(json.toMap());
			//return new ResponseEntity<Object>(json,HttpStatus.OK);
			//return MyUtility.convertListToJSONobj(chatMainRepository.findByCategoryAndBuildingCode(categoryEnum, buildingCode), "chat-room").toString();
		}
	}
	
	//채팅방 생성
	@PostMapping(value= "/chat-rooms",produces ="application/json" )
	public ResponseEntity<Object> createChatRoom(@RequestPart(name = "chat") ChatRoomDTO chat,@RequestPart MultipartFile file) {
		System.out.println(chat.getTag());
		JSONObject json = chatMainService.createChatRoom(chat, file);
		return ResponseEntity.ok(json.toMap());
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
	@GetMapping(value="/chat-rooms/{chatMain_id}",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getChatMain(@PathVariable(name = "chatMain_id") String chatMain_idstr) throws Exception {
		Long chatMain_id = Long.parseLong(chatMain_idstr);
		ChatMain chatMain = chatMainRepository.findChatMainEntityGraph(chatMain_id);
		return ResponseEntity.ok(chatMain.toJSON().toMap());
	}
	
	//채팅방 상태 업데이트
	@PatchMapping(value="/chat-rooms/{chatMain_id}")
	@Transactional
	public ResponseEntity<Object> updateRoomState(@PathVariable(name="chatMain_id") Long chatMain_id,
									@RequestParam(name="state")String state) {
		ChatState chatState = ChatState.valueOf(state.toUpperCase());
		ChatMain chatMain = chatMainRepository.findById(chatMain_id)
						.orElseThrow(()-> new CustomHttpException(ErrorCode.RESOURCE_NOT_FOUND));
		chatMain.changeState(chatState);
		return ResponseEntity.ok(chatMain.getState().name());
	}
	
	
	// 해당 채팅방접속중인 유저 list
	@GetMapping(value="/chat-rooms/{chatMain_id}/users",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getChatUserList(@PathVariable(name="chatMain_id")Long chatMain_id) throws Exception {
		JSONObject json =  MyUtility.convertListToJSONobj(chatUserRepository.findForRemit(chatMain_id),"users");

		return ResponseEntity.ok(json.toMap());
	}
//	@GetMapping(value="/chat-rooms/{chatMain_id}/users",produces = "application/json")
//	@Transactional(readOnly = true)
//	public ResponseEntity<Object> getChatUserList(@PathVariable(name="chatMain_id")Long chatMain_id) throws Exception {
//		JSONObject json =  MyUtility.convertListToJSONobj(chatUserRepository.findByChatMainId(chatMain_id), "users");
//		return ResponseEntity.ok(json.toMap());
//	}

	//송금상태 변경
	@PatchMapping(value="/chat-rooms/{chatMain_id}/users/{user_id}",produces = "application/json")
	@Transactional
	public ResponseEntity<Object> updateRemittanceState(@PathVariable(name="chatMain_id")Long chatMain_id,
										@PathVariable(name="user_id")String user_id) {
		ChatUser chatUser =  chatUserRepository.findByChatMainIdAndUserId(chatMain_id, user_id);
		chatUser.setState(RemittanceState.Y);
		return ResponseEntity.ok("success");
	}
	
	//해당 유저 퀵채팅
	@Transactional(readOnly = true)
	@GetMapping(value="/user/{user_id}/quick-chatting",produces = "application/json")
	public ResponseEntity<Object> getQuickChat(@PathVariable(name ="user_id")String user_id) {
		
		List<String> list = quickChatRepository.findByUserId(user_id).getMsgList();
		JSONObject json = MyUtility.convertStringListToJSONObject(list, "quickChat");
		json.put("user_id", user_id);
		return ResponseEntity.ok(json.toMap());
	}
	
	//참여중인 채팅방
	@Transactional(readOnly = true)
	@GetMapping(value="/chat-rooms/progressing/user/{user_id}",produces = "application/json")
	public ResponseEntity<Object> getProgressingChatList(@PathVariable(name="user_id") String user_id) throws Exception {
		JSONObject json = MyUtility.convertListToJSONobj(chatMainRepository.findByUserIdInChatUser(user_id), "chat-room");
		return ResponseEntity.ok(json.toMap());
	}
	
	//상태,방장아이디로 조회
	@Transactional(readOnly= true)
	@GetMapping(value="/chat-rooms/user/{user_id}",produces = "application/json")
	public ResponseEntity<Object> getChatListByStateAndUserId(@PathVariable(name="user_id")String user_id,@RequestParam(name="state") String state) throws Exception {
		
		ChatState chatState = ChatState.valueOf(state.toUpperCase());
		List<ChatMain> list = chatMainRepository.findByStateAndUserId(chatState, user_id);
		JSONObject json;
		if(ChatState.END.equals(chatState)) {
			json = MyUtility.convertListToJSONobjIgnoringCurrentCount(list, "chat-room");
		} else {
			json = MyUtility.convertListToJSONobj(list, "chat-room");
		}
		
		
		return ResponseEntity.ok(json.toMap());
	}
	
	
	
	//카테고리 리스트
	@GetMapping(value="/categories",produces = "application/json")
	public ResponseEntity<Object> getCategoryList(){
		return ResponseEntity.ok(MyUtility.convertCategoryToJSONobj("categories").toMap());
	}
	
	@GetMapping(value = "/chat-rooms/tag/most-wanted",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getMostWantedTagList() {
		List<String> list = tagRepository.findTop10Tag();
		JSONObject json = MyUtility.convertStringListToJSONObject(list, "tags");
		return ResponseEntity.ok(json.toMap());
	}
	@GetMapping(value="/chat-rooms/tag/{tag}",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getChatListByTag(@PathVariable("tag")String tag) throws Exception {
		List<ChatMain> list = chatMainRepository.findByTagName(tag);
		JSONObject json = MyUtility.convertListToJSONobj(list, "chat-room");
		return ResponseEntity.ok(json.toMap());
	}
	@GetMapping(value="/chat-rooms/search/{keyWord}",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getChatListByKeyWord(@PathVariable("keyWord")String keyWord) throws Exception {
		List<ChatMain> list = chatMainRepository.findByTagNameOrTitleContains(keyWord, keyWord);
		JSONObject json = MyUtility.convertListToJSONobj(list, "chat-room");
		return ResponseEntity.ok(json.toMap());
	}
	
	@GetMapping(value="/chat-rooms/{chatMain_id}/banned-user",produces = "application/json")
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getBannedChatUserList(@PathVariable("chatMain_id")String chatMain_idstr) throws Exception {
		Long chatMain_id = Long.parseLong(chatMain_idstr);
		List<ChatUser> list = chatUserRepository.findByChatMainIdAndBanState(chatMain_id, BanState.BANNED);
		JSONObject json =  MyUtility.convertListToJSONobj(list, "users");
		return ResponseEntity.ok(json.toMap());
	}
}
