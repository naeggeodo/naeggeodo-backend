package com.naeggeodo.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.jwt.JwtTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService service;
    private final JwtTokenProvider jwtToken;
    private final JwtTokenService jwtService;
    
    //소셜로그인으로부터 인증코드를 받아와 OAuthLogin으로 redirecte
    @RequestMapping(value = "login/OAuth/{provider}")
    public void getCode(@PathVariable String provider, HttpServletResponse response) {
    	String uri = service.getOAuthLoginUrl(provider); 

    	try {
    		response.setHeader("method", "POST");
			response.sendRedirect(uri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @RequestMapping(value = "oauth/getInfo/{provider}")
    public String OAuthLogin(@RequestParam("code") String code, @PathVariable String provider) throws JSONException, Exception {

    	return new ObjectMapper().writeValueAsString(
    			jwtService.createJwtToken(service.getAuth(code, provider)));
    }
    
    @PostMapping(value = "jwt/getUserId")
    public ResponseEntity<?> jwtUserTest(@RequestParam("token")String token){
    	Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    	logger.info(jwtToken.getSubject(token));
    	
    	return ResponseEntity.ok(jwtToken.getSubject(token));
    }
}
