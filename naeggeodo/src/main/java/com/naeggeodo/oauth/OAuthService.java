package com.naeggeodo.oauth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;

import com.naeggeodo.jwt.dto.RefreshTokenResponse;

import com.naeggeodo.oauth.config.InMemoryProviderRepository;
import com.naeggeodo.oauth.config.OauthProvider;
import com.naeggeodo.oauth.dto.KakaoOAuthDto;
import com.naeggeodo.oauth.dto.NaverOAuthDto;
import com.naeggeodo.oauth.dto.OAuthDto;
import com.naeggeodo.oauth.dto.OAuthDtoMapper;
import com.naeggeodo.oauth.dto.OauthAuthorized;
import com.naeggeodo.oauth.dto.SimpleUser;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OAuthService {

	@Autowired
    private InMemoryProviderRepository inMemoryProviderRepository;
	@Autowired
	private SocialLogin oauthDetail;
    private UserRepository userRepository;
    private OAuthDtoMapper oauthMapper;

    @Transactional
    public SimpleUser getAuth(String code, String providerName) throws JSONException, Exception {
    	Map<String, String> requestHeaders = new HashMap<>();
    	
    	log.info("getAuth : ");
    	OAuthDto oauthUserInfo = setOAuthDto(providerName);    	
    	OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);//application.yml에 등록된 oauth2 정보
    	
    	OauthAuthorized authorization = oauthDetail.requestAccessToken(code, provider);
    	
    	requestHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    	requestHeaders.put("Authorization", "Bearer "+authorization.getAccessToken()); //전부다 String형일 때. RestTemplate 때문에 생략가능
    	
    	String responseBody = oauthDetail.get(provider.getUserInfoUrl(), requestHeaders);
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//해당 필드가 없을경우 무시
    	new Users().setJoindate(LocalDateTime.now());
    	try {
    		oauthUserInfo = objectMapper.readValue(
    				responseBody, 
    				oauthUserInfo.getClass());
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return getUser(oauthUserInfo);
    	
    }
    
    public SimpleUser getAuth(OauthAuthorized oauthToken, String providerName) {
    	log.info("Mobile getAuth : " + oauthToken.toString());
    	OAuthDto oauthUserInfo = setOAuthDto(providerName);
    	
    	return getUser(
    			oauthDetail.requestUserInfo(
	    			inMemoryProviderRepository.findByProviderName(providerName).getUserInfoUrl(),
	    			oauthToken,
	    			oauthUserInfo
	    			)
    			);
    }
    
    //prider값에따라 해당 dto 반환
    private OAuthDto setOAuthDto(String providerName) {
    	switch(providerName) {
    	case "naver":
    		return new NaverOAuthDto();
    	case "kakao":
    		return new KakaoOAuthDto();
    	default:
    		throw new NullPointerException();
    	}
    }
    
    @Transactional 
    @NotFound(action = NotFoundAction.IGNORE)
    private SimpleUser getUser(OAuthDto oauthDto) {
    	Users user = userRepository.findById(oauthDto.getId()).orElse(null);
    	
    	if(user==null) {
    		user = oauthMapper.mappingUser(oauthDto);
    		user.setAuthority(Authority.MEMBER);
    		user.setJoindate(LocalDateTime.now());
    		
    		userRepository.save(user);
    		user = userRepository.findById(oauthDto.getId()).get();
    	}
    	
    	return new SimpleUser(user.getId(), user.getAddr(), user.getAuthority());
    }


   
}

