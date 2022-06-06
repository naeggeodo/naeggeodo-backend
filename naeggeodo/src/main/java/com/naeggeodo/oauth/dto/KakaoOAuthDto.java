package com.naeggeodo.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoOAuthDto implements OAuthDto{
	
	@JsonProperty("id")
	private String id;
	private Properties properties;
	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;
	
	@Data
	class Properties {
		private String nickname;
		private String profile_image;
		private String thumbnail_image;
	}
	
	@Data
	class KakaoAccount {
		private boolean profile_nickname_needs_agreement;
		private boolean has_email;
		private boolean email_needs_agreement;
		private boolean is_email_valid;
		private boolean is_email_verified;
		private String email;

	}
	
}
