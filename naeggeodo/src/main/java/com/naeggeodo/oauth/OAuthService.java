package com.naeggeodo.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.entity.user.Authority;
import com.naeggeodo.entity.user.Users;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.oauth.config.InMemoryProviderRepository;
import com.naeggeodo.oauth.config.OauthProvider;
import com.naeggeodo.oauth.config.OauthTokenResponse;
import com.naeggeodo.oauth.dto.KakaoOAuthDto;
import com.naeggeodo.oauth.dto.NaverOAuthDto;
import com.naeggeodo.oauth.dto.OAuthDto;
import com.naeggeodo.oauth.dto.OAuthDtoMapper;
import com.naeggeodo.oauth.dto.SimpleUser;
import com.naeggeodo.user.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OAuthService {
    private InMemoryProviderRepository inMemoryProviderRepository;
    private UserRepository userRepository;
    private OAuthDtoMapper oauthMapper;

    @Transactional
    public SimpleUser getAuth(String code, String providerName) throws JSONException, Exception {
    	Logger logger = LoggerFactory.getLogger(this.getClass());
    	Map<String, String> requestHeaders = new HashMap<>();
    	
    	
    	OAuthDto oauthUserInfo = setOAuthDto(providerName);    	
    	OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);//application.yml에 등록된 oauth2 정보
    	
    	OauthTokenResponse authorization = getToken(code, provider);
    	
    	requestHeaders.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    	requestHeaders.put("Authorization", "Bearer "+authorization.getAccessToken()); //전부다 String형일 때. RestTemplate 때문에 생략가능
    	
    	String responseBody = get(provider.getUserInfoUrl(),requestHeaders);
    	
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
    
    public OauthTokenResponse getTokenTest(String code, String providerName) throws JsonMappingException, JsonProcessingException {
    	Logger logger = LoggerFactory.getLogger(this.getClass());
    	
    	OAuthDto oauthUserInfo = setOAuthDto(providerName);    	
    	OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);//application.yml에 등록된 oauth2 정보

    	logger.info(code);
    	
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity<>(headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(provider.getTokenUrl())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", provider.getClientId())
//                .queryParam("redirect_uri", provider.getRedirectUrl())
                .queryParam("redirect_uri", "http://localhost:8080/login/getToken/kakao")
                .queryParam("code", code)
                .queryParam("client_secret", provider.getClientSecret());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                request,
                String.class
        );
        log.info(responseEntity.getBody());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return mapper.readValue(responseEntity.getBody(), OauthTokenResponse.class);
        }
        return new OauthTokenResponse();
    	
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
    
    //소셜플랫폼에서 access token 값을 가져옴
    private OauthTokenResponse getToken(String code, OauthProvider provider) throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity<>(headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(provider.getTokenUrl())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", provider.getClientId())
                .queryParam("redirect_uri", provider.getRedirectUrl())
                .queryParam("code", code)
                .queryParam("client_secret", provider.getClientSecret());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                request,
                String.class
        );
        log.info(responseEntity.getBody());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return mapper.readValue(responseEntity.getBody(), OauthTokenResponse.class);
        }
        return new OauthTokenResponse();
    }
    
    private SimpleUser getUser(OAuthDto oauthDto) {
    	Users user = userRepository.findOne(oauthDto.getId());
    	
    	if(user==null) {
    		user = oauthMapper.mappingUser(oauthDto);
    		user.setAuthority(Authority.MEMBER);
    		user.setJoindate(LocalDateTime.now());
    		
    		userRepository.save(user);
    		user =  userRepository.findOne(oauthDto.getId());
    	}
    	
    	return new SimpleUser(user.getId(), user.getAddr(), user.getAuthority());
    }
    
    private String get(String apiUrl, Map<String, String> requestHeaders){
    	HttpURLConnection con = connect(apiUrl);
    	try {
    		con.setRequestMethod("GET");
    		for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
    			con.setRequestProperty(header.getKey(), header.getValue());
    		}
    		
    		int responseCode = con.getResponseCode();
    		if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
    			return readBody(con.getInputStream());
    		} else { // 에러 발생
    			return readBody(con.getErrorStream());
    		}
    	} catch (IOException e) {
    		throw new RuntimeException("API 요청과 응답 실패", e);
    	} finally {
    		con.disconnect();
    	}
    }
    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
    
    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

	public String getOAuthLoginUrl(String providerName) {
		OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);
        
		return provider.getOAuthLoginUri();

	}
}
