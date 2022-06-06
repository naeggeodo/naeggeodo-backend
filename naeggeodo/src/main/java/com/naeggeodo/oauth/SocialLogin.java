package com.naeggeodo.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.oauth.config.OauthProvider;
import com.naeggeodo.oauth.dto.OAuthDto;
import com.naeggeodo.oauth.dto.OauthAuthorized;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SocialLogin {
	private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
	private static final String HEADER_NAME_CONTENT_TYPE = "Content-type";
	private static final String HEADER_VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	

    protected OAuthDto requestUserInfo(String userInfoRequestUrl, OauthAuthorized oauthToken, OAuthDto responseType) {
        HttpHeaders headers = requestUserInfoHeaders(oauthToken);
        RestTemplate restTemplate = new RestTemplate();
        try {
            OAuthDto userResponse = restTemplate.exchange(
                    userInfoRequestUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    responseType.getClass()
            ).getBody();
            return userResponse;
        } catch (HttpStatusCodeException e) {
        	log.info(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    protected OauthAuthorized requestAccessToken(String code, OauthProvider provider) throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> request = new HttpEntity<>(headers);

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
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        log.info(responseEntity.getBody().toString());
        
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return mapper.readValue(responseEntity.getBody(), OauthAuthorized.class);
        }
        return new OauthAuthorized();
    	
    }

    protected HttpHeaders requestUserInfoHeaders(OauthAuthorized oauthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME_AUTHORIZATION, "Bearer " + oauthToken.getAccessToken());
        headers.add(HEADER_NAME_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE);
        return headers;
    }

//    protected HttpEntity<MultiValueMap<String, String>> generateAccessTokenRequest(String code) {
//        MultiValueMap<String, String> param = this.generateAccessTokenRequestParam(code);
//        HttpHeaders headers = this.generateAccessTokenRequestHeaders();
//        return new HttpEntity<>(param, headers);
//    }

    protected HttpHeaders generateAccessTokenRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_NAME_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE);
        return headers;
    }
    
    protected String get(String apiUrl, Map<String, String> requestHeaders){
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
}
