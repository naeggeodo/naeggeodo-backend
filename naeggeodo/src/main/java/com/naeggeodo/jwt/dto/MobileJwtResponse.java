package com.naeggeodo.jwt.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.naeggeodo.oauth.dto.SimpleUser;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
public
class MobileJwtResponse extends JwtResponse {
    private String refreshToken;

    public MobileJwtResponse(String accessToken, String type, SimpleUser user) {
        super(accessToken, type, user);
    }

    public MobileJwtResponse(String accessToken, String type, SimpleUser user, String refreshToken) {
        super(accessToken, type, user);
        this.refreshToken = refreshToken;
    }
}
