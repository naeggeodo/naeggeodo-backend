package com.naeggeodo.dto;


import com.naeggeodo.entity.chat.ChatDetailType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MessageDTO {
	private Long chatMain_id;
	private String contents;
	//전송한 user_id
	private Long sender;
	private ChatDetailType type;
	
}
