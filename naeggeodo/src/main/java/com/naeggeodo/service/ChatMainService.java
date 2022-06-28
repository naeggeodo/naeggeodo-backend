package com.naeggeodo.service;

import com.naeggeodo.dto.ChatRoomDTO;
import com.naeggeodo.entity.chat.*;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatDetailRepository;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.TagRepository;
import com.naeggeodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatMainService {

	private final ChatMainRepository chatMainRepository;
	private final CloudinaryService cloudinaryService;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;
	private final ChatDetailRepository chatDetailRepository;
	

	@Transactional
	public JSONObject createChatRoom(ChatRoomDTO dto,MultipartFile file) {
		Users user = userRepository.getById(dto.getUser_id());

		ChatMain chatMain = ChatMain.create(dto,user);
		List<String> tagStringList = dto.getTag();
		List<Tag> tagList = new ArrayList<>();
		ChatMain savedChatMain = chatMainRepository.save(chatMain);
		if(tagStringList != null){
			for (String name : tagStringList) {
				tagList.add(Tag.create(savedChatMain, name));
			}
			tagRepository.saveAll(tagList);
		}
		//async
		if(file != null)
			cloudinaryService.upload(file, "chatMain/"+savedChatMain.getId(),savedChatMain.getId());
		else
			chatMain.setDefaultImgPath();

		chatDetailRepository.save(ChatDetail.create("채팅방이 생성 되었습니다",user,savedChatMain, ChatDetailType.CREATED));

		JSONObject json = new JSONObject();
		json.put("chatMain_id", savedChatMain.getId());
		return json;
	}

	@Transactional
	public JSONObject copyChatRoom(Long targetId, OrderTimeType orderTimeType){
		ChatMain targetChatMain =  chatMainRepository.findTagEntityGraph(targetId);
		ChatMain savedChatMain = chatMainRepository.save(targetChatMain.copy(orderTimeType));
		List<Tag> saveTags = savedChatMain.copyTags(targetChatMain.getTag());
		tagRepository.saveAll(saveTags);

		chatDetailRepository.save(ChatDetail.create("채팅방이 생성 되었습니다",savedChatMain.getUser(),savedChatMain, ChatDetailType.CREATED));
		JSONObject json = new JSONObject();
		json.put("chatMain_id",savedChatMain.getId());
		return json;
	}

}
