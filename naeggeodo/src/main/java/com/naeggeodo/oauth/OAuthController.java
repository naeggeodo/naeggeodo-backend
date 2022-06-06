package com.naeggeodo.oauth;


import java.util.Map;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenService;
import com.naeggeodo.jwt.dto.RefreshTokenRequest;
import com.naeggeodo.jwt.dto.RefreshTokenResponse;
import com.naeggeodo.oauth.dto.OauthAuthorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService service; 
//    private final JwtTokenProvider jwtToken;
    private final JwtTokenService jwtService;

    
    @GetMapping(value= "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthCode(@RequestParam String code, @PathVariable String provider) throws JSONException, Exception {
    	log.info("OAUthCode : "+code);
    	
    	
    	return ResponseEntity.ok(code);
    	
    }

    @PostMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestBody Map<String,String> request, @PathVariable String provider) throws JSONException, Exception {
    	log.info("OAUthLogin: ");

    	return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                jwtService.createJwtToken(service.getAuth(request.get("code"), provider))));
    	
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) throws Exception {
    	RefreshTokenResponse jwtResponse = jwtService.refreshToken(request.getRefreshToken());
    	
    	return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping(value = "login/mobil/{provider}")
    public ResponseEntity<?> MobileLogin(@RequestBody OauthAuthorized request, @PathVariable String provider) throws JsonProcessingException{
    	return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jwtService.createJwtToken(
    				service.getAuth(request, provider)
    			)));

    }

//    @PostMapping("/refreshtoken")
//    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
//    	RefreshTokenResponse jwtResponse = service.refreshToken(request.getRefreshToken());
//
//    	return ResponseEntity.ok(jwtResponse);
//    }
}

