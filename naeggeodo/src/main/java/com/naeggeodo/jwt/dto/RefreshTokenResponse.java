package com.naeggeodo.jwt.dto;

import lombok.Builder;
import lombok.Data;

public class RefreshTokenResponse {
	String accessToken;
	String refreshToken;

	public RefreshTokenResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
}
