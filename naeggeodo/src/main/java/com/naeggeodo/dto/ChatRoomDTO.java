package com.naeggeodo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChatRoomDTO {
	@NotBlank
	private String buildingCode;
	@NotBlank
	private String address;
	@NotBlank
	private String category;
	private String link;
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
