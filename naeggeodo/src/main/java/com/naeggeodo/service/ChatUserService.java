package com.naeggeodo.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.chat.ChatUser;
import com.naeggeodo.entity.chat.QuickChat;
import com.naeggeodo.entity.chat.RemittanceState;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.ChatUserRepository;
import com.naeggeodo.repository.QuickChatRepository;
import com.naeggeodo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatUserService {
	private final ChatUserRepository chatUserRepository;
	private final UserRepository userRepository;
	private final ChatMainRepository chatMainRepository;
	private final QuickChatRepository quickChatRepository;
	
	// dto로 저장
	@Transactional
	public void save(MessageDTO messageDTO,String session_id) {
		ChatMain chatMain =  chatMainRepository.findOne(messageDTO.getChatMain_id());
		Users user = userRepository.findOne(messageDTO.getSender());
		chatUserRepository.save(ChatUser.create(user, chatMain,session_id));
	}
	// 변수로 저장
	@Transactional
	public void save(Long chatMain_id,String sender,String session_id) {
		ChatMain chatMain =  chatMainRepository.findOne(chatMain_id);
		Users user = userRepository.findOne(sender);
		chatUserRepository.save(ChatUser.create(user, chatMain,session_id));
	}
	
	//이 사람이 채팅방에 있냐? (dto)
	@Transactional
	public boolean isExist(MessageDTO messageDTO) {
		Long cnt = chatUserRepository.check(messageDTO.getChatMain_id(), messageDTO.getSender());
		
		if(cnt == 1L) {
			return true;
		}
		return false;
	}
	
	//이 사람이 채팅방에 있냐?(변수)
	@Transactional
	public boolean isExist(Long chatMain_id,String sender) {
		Long cnt = chatUserRepository.check(chatMain_id,sender);
		
		if(cnt == 1L) {
			return true;
		}
		return false;
	}
	
	// 유저 delete(dto)
	@Transactional
	public void exit(MessageDTO messageDTO) {
		chatUserRepository.deleteById(messageDTO.getChatMain_id(), messageDTO.getSender());
	}
	
	//유저 delete(변수)
	@Transactional
	public void exit(Long chatMain_id,String user_id) {
		chatUserRepository.deleteById(chatMain_id, user_id);
	}
	//유저 delete(session)
	@Transactional
	public void exit(String session_id) {
		chatUserRepository.deleteBySessionId(session_id);
	}
	
	//sessionid get 밴할때 씀
	@Transactional
	public String getSession_id(MessageDTO messageDTO) {
		Long chatMain_id = messageDTO.getChatMain_id();
		//밴할 유저 id
		String user_id = messageDTO.getContents();
		return chatUserRepository.findByChatMainAndUserId(chatMain_id, user_id).getSession_id();
	}
	@Transactional
	public String getSession_id(Long chatMain_id, String user_id) {
		return chatUserRepository.findByChatMainAndUserId(chatMain_id, user_id).getSession_id();
	}
	
	//update sessionid
	@Transactional
	public void updateSession_id(Long chatMain_id,String user_id,String session_id) {
		ChatUser chatUser = chatUserRepository.findByChatMainAndUserId(chatMain_id, user_id);
		chatUser.setSession_id(session_id);
	}
	
	//현재 접속 인원 list to json
	@Transactional
	public List<ChatUser> currentList(Long chatMain_id) {
		return chatUserRepository.findByChatMainId(chatMain_id);
	}
//	@Transactional
//	public JSONArray currentList(Long chatMain_id) {
//		
//		JSONArray arr_json = new JSONArray();
//		List<ChatUser> list = chatUserRepository.findByChatMainId(chatMain_id);
//		for (int i = 0; i <list.size() ; i++) {
//			JSONObject json = new JSONObject();
//			json.put("idx", i);
//			json.put("user_id", list.get(i).getUser().getId());
//			json.put("remittanceState", list.get(i).getState().name());
//			arr_json.put(json);
//		}
//		return arr_json;
//	}
	
	//송금상태 update
	@Transactional
	public void updateRemittanceStateToY(Long chatMain_id,String user_id) {
		ChatUser chatUser = chatUserRepository.findByChatMainAndUserId(chatMain_id, user_id);
		chatUser.setState(RemittanceState.Y);
	}
}
