package com.naeggeodo.jwt.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.oauth.dto.SimpleUser;

import lombok.Builder;
import lombok.ToString;

@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JwtResponse {
	private String accessToken;
	private String refreshToken;
	private String type;
	private SimpleUser user;
//	private String userId;
//	private String addr;

	public JwtResponse(String accessToken, String refreshToken, String type, SimpleUser user) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.type = type;
		this.user = user;
//		this.userId = user.getId();
//		this.addr = user.getAddress();
	}
	
}
 