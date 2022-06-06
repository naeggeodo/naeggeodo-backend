package com.naeggeodo.oauth;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.naeggeodo.entity.user.Users;
import com.naeggeodo.oauth.dto.KakaoOAuthDto;
import com.naeggeodo.oauth.dto.NaverOAuthDto;
import com.naeggeodo.oauth.dto.OAuthDto;

import lombok.NoArgsConstructor;


@Component
@NoArgsConstructor
public class OAuthDtoMapper {
	
	public Users mappingUser(OAuthDto oauthDto) {
		if(oauthDto instanceof NaverOAuthDto) { return naverToUser(oauthDto);}
		else if(oauthDto instanceof KakaoOAuthDto) {return kakaoToUser(oauthDto);}
		else throw new IllegalArgumentException("지원하지 않는 소셜로그인입니다.");
	}
	
	private Users naverToUser(OAuthDto oauthDto){
		ModelMapper modelMapper = new ModelMapper();
		
		modelMapper.typeMap(NaverOAuthDto.class, Users.class).addMappings(mapper -> {
			mapper.map(src -> src.getResponse().getId(), Users::setId);
			mapper.map(src -> src.getResponse().getMobile(), Users::setPhone);
			mapper.map(src -> src.getResponse().getName(), Users::setNickname);
			mapper.map(src -> src.getResponse().getMobile(), Users::setPhone);
			mapper.map(src -> src.getResponse().getEmail(), Users::setEmail);
		});
		
		return modelMapper.map(oauthDto, Users.class);
	}
	
	private Users kakaoToUser(OAuthDto kakaoOauthDto) {
		ModelMapper modelMapper = new ModelMapper();
		
		modelMapper.typeMap(KakaoOAuthDto.class, Users.class).addMappings(mapper -> {
			mapper.map(src -> src.getProperties().getNickname(), Users::setNickname);
			mapper.map(src -> src.getKakaoAccount().getEmail(), Users::setEmail);
		});
		
		return modelMapper.map(kakaoOauthDto, Users.class);
		
	}
}
