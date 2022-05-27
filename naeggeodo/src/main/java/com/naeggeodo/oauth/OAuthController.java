package com.naeggeodo.oauth;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.jwt.JwtTokenService;
import com.naeggeodo.jwt.dto.RefreshTokenRequest;
import com.naeggeodo.jwt.dto.RefreshTokenResponse;

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
    public void getCode(@PathVariable String provider, HttpServletResponse response, HttpServletRequest request) {
    	//url에 필요한 데이터
    }
    @GetMapping(value= "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthCode(@RequestParam String code, @PathVariable String provider) throws JSONException, Exception {
    	log.info("OAUthCode : "+code);
    	
    	
    	return ResponseEntity.ok(code);
    	
    }


    @PostMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestBody Map<String,String> request, @PathVariable String provider) throws JSONException, Exception {
    	log.info(request.get("code"));
    	
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = new ObjectMapper().convertValue(jwtService.createJwtToken(service.getAuth(request.get("code"), provider)), new TypeReference<Map<String, String>>() {}); // (3)
        params.setAll(map);

    	HttpHeaders header = new HttpHeaders(params);
    	
    	return ResponseEntity.ok().headers(header).body("ok");
    	
    }

//    @PostMapping("/refreshtoken")
//    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
//    	RefreshTokenResponse jwtResponse = service.refreshToken(request.getRefreshToken());
//    	
//    	return ResponseEntity.ok(jwtResponse);
//    }
}
