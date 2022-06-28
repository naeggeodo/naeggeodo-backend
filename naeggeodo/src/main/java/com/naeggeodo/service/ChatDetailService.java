package com.naeggeodo.service;

import com.naeggeodo.dto.MessageDTO;
import com.naeggeodo.dto.TargetMessageDTO;
import com.naeggeodo.entity.chat.ChatDetail;
import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatDetailRepository;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatDetailService {
	private final ChatDetailRepository chatDetailRepository;
	private final UserRepository userRepository;
	private final ChatMainRepository chatMainRepository;
	
	//dto 로 저장
	@Transactional
	public void save(MessageDTO messageDTO) {
		Users user = userRepository.getById(messageDTO.getSender());
		ChatMain chatmain = chatMainRepository.getById(messageDTO.chatMain_idToLong());
		
		ChatDetail chatDetail = ChatDetail.create(messageDTO.getContents(), user, chatmain, messageDTO.getType());
		chatDetailRepository.save(chatDetail);
	}
	@Transactional
	public void save(TargetMessageDTO messageDTO) {
		Users user = userRepository.getById(messageDTO.getSender());
		ChatMain chatmain = chatMainRepository.getById(messageDTO.chatMain_idToLong());

		ChatDetail chatDetail = ChatDetail.create(messageDTO.getContents(), user, chatmain, messageDTO.getType());
		chatDetailRepository.save(chatDetail);
	}

	//기존 채팅 내역 불러오기
	@Transactional
	public List<ChatDetail> load(String chatMain_idStr, String user_id){
		Long chatMain_id = Long.parseLong(chatMain_idStr);
		
		return chatDetailRepository.load(chatMain_id, user_id);
	}
}
