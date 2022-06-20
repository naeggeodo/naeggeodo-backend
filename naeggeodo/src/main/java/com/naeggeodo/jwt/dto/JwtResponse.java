package com.naeggeodo.jwt.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.naeggeodo.oauth.dto.SimpleUser;

import lombok.ToString;

@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JwtResponse {
	private String accessToken;
	private String type;
	private String user_id;
	private String address;

	private String buildingCode;

	public JwtResponse(String accessToken, String type, SimpleUser user) {
		this.accessToken = accessToken;
		this.type = type;
//		this.user = user;
		this.user_id = user.getId();
		this.address = user.getAddress();
		this.buildingCode = user.getBuildingCode();
	}

}
 