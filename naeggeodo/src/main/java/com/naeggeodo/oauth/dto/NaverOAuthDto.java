package com.naeggeodo.oauth.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.naeggeodo.entity.user.Users;

import lombok.Data;

@Data
public class NaverOAuthDto implements OAuthDto{

	private String message;
	private Response response;
	
	@Data
	public class Response{
		private String id;
		private String gender;
		private String email;
		private String mobile;
		private String name;
	}

	@Override
	public String getId() {
		return response.getId();
	}
}
