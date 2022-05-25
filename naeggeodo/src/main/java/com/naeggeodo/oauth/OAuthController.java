package com.naeggeodo.oauth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
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
    @RequestMapping(value = "login/{provider}")
    public void getCode(@PathVariable String provider, HttpServletResponse response, HttpServletRequest request) {
    	//url에 필요한 데이터
    }
    
    @GetMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthCallback(){
    	return ResponseEntity.ok("ok");
    }
    
    @PostMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestParam("code") String code, @PathVariable String provider) throws JSONException, Exception {
    	
    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = new ObjectMapper().convertValue(jwtService.createJwtToken(service.getAuth(code, provider)), new TypeReference<Map<String, String>>() {}); // (3)
        params.setAll(map);

    	HttpHeaders header = new HttpHeaders(params);
    	
    	return ResponseEntity.ok().headers(header).body("ok");
    	
    }

//    @PostMapping("/refreshtoken")
//    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//        
//        if(jwtToken.validateToken(requestRefreshToken)) {
//    	  
//        }
//      return refreshTokenService.findByToken(requestRefreshToken)
//          .map(refreshTokenService::verifyExpiration)
//          .map(RefreshToken::getUser)
//          .map(user -> {
//            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
//            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
//          })
//          .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
//              "Refresh token is not in database!"));
//    
//    }
}
