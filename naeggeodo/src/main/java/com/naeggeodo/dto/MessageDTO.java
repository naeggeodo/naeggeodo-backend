package com.naeggeodo.dto;


import com.naeggeodo.entity.chat.ChatDetailType;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MessageDTO {

	private String chatMain_id;
	private String contents;
	//전송한 user_id
	private String sender;
	private ChatDetailType type;
	public Long chatMain_idToLong(){
		return Long.parseLong(chatMain_id);
	}
}
