package com.naeggeodo.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.jwt.JwtTokenService;
import com.naeggeodo.jwt.dto.RefreshTokenRequest;
import com.naeggeodo.oauth.config.OauthTokenResponse;

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
    	log.info(uri);
    	try {
			response.sendRedirect(uri);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
//    @RequestMapping(value = "oauth/getInfo/{provider}")
//    public ResponseEntity<?> OAuthLogin(@RequestParam("code") String code, @PathVariable String provider) throws JSONException, Exception {
//    	log.info(code);
//    	return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
//    			jwtService.createJwtToken(service.getAuth(code, provider))));
//    }
    
    @RequestMapping(value = "oauth/getInfo/{provider}")
    public String OAuthLogin(@RequestParam("code") String code, @PathVariable String provider) throws JSONException, Exception {
    	log.info(code);
    	return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
    			jwtService.createJwtToken(service.getAuth(code, provider)));
    }
    
    @RequestMapping(value = "login/getToken/{provider}")
    public OauthTokenResponse tokenTest(@RequestParam("code") String code, @PathVariable String provider) throws JsonMappingException, JsonProcessingException {
    	
    	return service.getTokenTest(code, provider);
    
//    @PostMapping("/refreshtoken")
//    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//        
//        if(jwtToken.validateToken(requestRefreshToken)) {
//    	  
//        }
//      return refreshTokenService.findByToken(requestRefreshToken)
//          .map(refreshTokenService::verifyExpiration)
//  s        .map(RefreshToken::getUser)
//          .map(user -> {
//            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
//            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
//          })
//          .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
//              "Refresh token is not in database!"));
    }
}
