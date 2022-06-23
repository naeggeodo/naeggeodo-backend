package com.naeggeodo.oauth;


import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import org.json.JSONObject;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.jwt.JwtTokenService;
import com.naeggeodo.jwt.dto.RefreshTokenRequest;
import com.naeggeodo.jwt.dto.RefreshTokenResponse;
import com.naeggeodo.oauth.dto.OauthAuthorized;
import com.naeggeodo.oauth.dto.SimpleUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;


@Slf4j

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService service; 
    private final JwtTokenService jwtService;
    private final JwtTokenProvider jwtProvider;

    
    @GetMapping(value= "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthCode(@RequestParam String code, @PathVariable String provider) throws JSONException, Exception {
    	log.info("OAUthCode : "+code);
    	
    	return ResponseEntity.ok(code);
    }

    @PostMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestBody Map<String,String> request, @PathVariable String provider, HttpServletResponse response) throws JSONException, Exception {

       log.info("OAUthLogin: ");

       SimpleUser user = service.getAuth(request.get("code"), provider);
       
        ResponseCookie cookie = ResponseCookie.from("refreshToken", jwtProvider.createRefreshToken(user.getId()))

                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());



       return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                jwtService.createJwtToken(user)));
       

    }
    
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(HttpServletResponse response
            ,@CookieValue(value = "refreshToken")Cookie cookie){

        log.info("Logout: ");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    	return ResponseEntity.ok(true);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
    	String refreshToken = request.getHeader("Set-Cookie");
    	refreshToken = refreshToken.substring(refreshToken.indexOf("=")+1);
    	
    	JSONObject jwtResponse = new JSONObject(); 
    	jwtResponse.put("accessToken", jwtService.refreshToken(refreshToken).getAccessToken());
    	
    	
    	
    	return ResponseEntity.ok(jwtResponse.toMap());
    }
    
    @PostMapping(value = "login/mobil/{provider}")
    public ResponseEntity<?> MobileLogin(@RequestBody OauthAuthorized request, @PathVariable String provider) throws JsonProcessingException{
    	return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jwtService.createJwtToken(
    				service.getAuth(request, provider)
    			)));
    }
    
}

