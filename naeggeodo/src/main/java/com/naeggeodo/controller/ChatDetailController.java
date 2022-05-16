package com.naeggeodo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.naeggeodo.service.ChatDetailService;
import com.naeggeodo.util.MyUtility;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatDetailController {
	private final ChatDetailService chatDetailService;
	
	@GetMapping(value ="/chat/messages/{chatMain_id}/{user_id}",produces = "application/json")
	public String load(@PathVariable String chatMain_id, @PathVariable String user_id) throws Exception {
		return MyUtility.convertListToJSONobj(chatDetailService.load(chatMain_id, user_id), "messages").toString();
	}
}
