package com.naeggeodo.jwt;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.jwt.dto.JwtResponse;
import com.naeggeodo.jwt.dto.MobileJwtResponse;
import com.naeggeodo.jwt.dto.RefreshTokenResponse;
import com.naeggeodo.oauth.dto.SimpleUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenProvider jwtProvider;

    public JwtResponse createJwtToken(SimpleUser user) {
        String id = user.getId();

        return new JwtResponse(jwtProvider.createToken(id),
                "Bearer",
                user);
    }

    public JwtResponse mobileCreateJwtToken(SimpleUser user) {
        String id = user.getId();

        return new MobileJwtResponse(jwtProvider.createToken(id),
                "Bearer",
                user,
                jwtProvider.createRefreshToken(id)
        );
    }

    public RefreshTokenResponse refreshToken(String token) throws CustomHttpException {
        log.info("refreshtoken");
        if (!jwtProvider.validateToken(token)) {
            throw new CustomHttpException(ErrorCode.EXPIRED_TOKEN);
        }

        String userId = jwtProvider.getSubject(token);
        return new RefreshTokenResponse(jwtProvider.createToken(userId), token);
    }
//값이 이상하면 
}