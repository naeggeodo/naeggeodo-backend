package com.naeggeodo.oauth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naeggeodo.jwt.JwtTokenProvider;
import com.naeggeodo.jwt.JwtTokenService;
import com.naeggeodo.oauth.dto.OauthAuthorized;
import com.naeggeodo.oauth.dto.SimpleUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Slf4j

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService service;
    private final JwtTokenService jwtService;
    private final JwtTokenProvider jwtProvider;



    @PostMapping(value = "login/OAuth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestBody Map<String,String> request, @PathVariable String provider, HttpServletResponse response) throws Exception {

        log.info("OAUthLogin: ");

        SimpleUser user = service.getAuth(request.get("code"), provider);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", jwtProvider.createRefreshToken(user.getId()))

                .maxAge(12 * 30 * 24 * 60 * 60)
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
    public ResponseEntity<?> refreshtoken(@CookieValue(value = "refreshToken",required = false)Cookie cookie,
                                          HttpServletResponse response) {
        log.info(cookie==null?"cookie is null":"cookie isn't null");
    	String refreshToken = cookie.getValue();
        log.info("cookie.getValue() = {}",refreshToken);

    	JSONObject jwtResponse = new JSONObject(); 
    	String accessToken =jwtService.refreshToken(refreshToken).getAccessToken();

        jwtResponse.put("accessToken",accessToken);

    	return ResponseEntity.ok(jwtResponse.toMap());

    }

    @PostMapping(value = "login/mobil/{provider}")
    public ResponseEntity<?> MobileLogin(@RequestBody OauthAuthorized request, @PathVariable String provider) throws JsonProcessingException{
        return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jwtService.createJwtToken(
                service.getAuth(request, provider)
        )));
    }

}