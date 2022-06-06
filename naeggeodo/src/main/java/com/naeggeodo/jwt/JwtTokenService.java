package com.naeggeodo.jwt;

import org.springframework.stereotype.Service;

import com.naeggeodo.jwt.dto.JwtResponse;
import com.naeggeodo.jwt.dto.RefreshTokenResponse;
import com.naeggeodo.oauth.dto.SimpleUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
	private final JwtTokenProvider jwtProvider;
	
	public JwtResponse createJwtToken(SimpleUser user) {
		  String id = user.getId();
		  
		  return new JwtResponse(jwtProvider.createToken(id),
					  jwtProvider.createRefreshToken(id),
					  "Bearer",
					  user);
	}

    public RefreshTokenResponse refreshToken(String token) throws Exception{
        String userId = jwtProvider.getSubject(token);
    	
    	
    	if (!jwtProvider.validateToken(token)) {
            throw new Exception("Refresh token was expired. Please ma   ke a new signin request");
        }
    	return new RefreshTokenResponse(jwtProvider.createToken(userId),token);
    }

}