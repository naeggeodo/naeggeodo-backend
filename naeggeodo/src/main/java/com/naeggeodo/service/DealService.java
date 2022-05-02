package com.naeggeodo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naeggeodo.entity.chat.ChatMain;
import com.naeggeodo.entity.deal.Deal;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.repository.ChatMainRepository;
import com.naeggeodo.repository.DealRepository;
import com.naeggeodo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {
	private final DealRepository dealRepository;
	private final UserRepository userRepository;
	private final ChatMainRepository chatMainRepository;
	
	
	@Transactional
	public void createDeal(Long chatMain_id, Long user_id) {
		Users user = userRepository.findOne(user_id);
		ChatMain chatMain = chatMainRepository.findOne(chatMain_id);
		Deal deal = Deal.create(user, chatMain);
		dealRepository.save(deal);
	}
	
//	@Transactional
//	public JSONArray getDealListToJSON(Long chatMain_id) {
//		List<Deal> dealList =  dealRepository.findByChatMain(chatMain_id);
//		JSONArray arr_json = new JSONArray(); 
//		for (int i = 0; i < dealList.size(); i++) {
//			JSONObject json = new JSONObject();
//			json.put("idx", i);
//			json.put("user_id",dealList.get(i).getUser().getId());
//			arr_json.add(json);
//		}
//		return arr_json;
//		
//	}
}
