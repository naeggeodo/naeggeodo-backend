package com.naeggeodo.jwt;

import org.springframework.stereotype.Service;

import com.naeggeodo.jwt.dto.JwtResponse;
import com.naeggeodo.oauth.dto.SimpleUser;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

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


}