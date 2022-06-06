package com.naeggeodo.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChatRoomDTO {
	@NotBlank
	private String addr;
	@NotBlank
	private String category;
	private String link;
	@NotBlank
	private String place;
	@NotBlank
	private String title;
	@NotBlank
	private String user_id;
	@NotBlank
	private String orderTimeType;
	private List<String> tag;

	private int maxCount;
}
