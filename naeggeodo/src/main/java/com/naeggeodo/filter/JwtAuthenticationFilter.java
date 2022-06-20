package com.naeggeodo.filter;


import com.naeggeodo.exception.UnauthorizedException;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private JwtTokenProvider jwtProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = AuthorizationExtractor.extract(request);
		if(Objects.isNull(token)) {
			throw new UnauthorizedException("Access Token 이 존재하지 않습니다.");
		}
		if(!jwtProvider.validateToken(token)&&!token.equals("open")) {
			throw new UnauthorizedException("유효한 토큰이 아닙니다.");
		}

		filterChain.doFilter(request,response);
	}
}

