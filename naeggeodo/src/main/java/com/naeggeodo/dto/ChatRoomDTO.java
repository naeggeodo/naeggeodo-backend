package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChatRoomDTO {
	private String addr;
	private Category category;
	private String link;
	private String place;
	private String title;
	private String user_id;
}
