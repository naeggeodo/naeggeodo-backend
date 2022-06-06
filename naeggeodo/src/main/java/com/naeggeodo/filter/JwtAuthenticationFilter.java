package com.naeggeodo.filter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.naeggeodo.exception.UnauthorizedException;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtProvider;
    
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = AuthorizationExtractor.extract(request);
		
		if(Objects.isNull(token)) {
			throw new UnauthorizedException("Access Token 이 존재하지 않습니다.");
		}
		if(!jwtProvider.validateToken(token)) {
			throw new UnauthorizedException("유효한 토큰이 아닙니다.");
		}
        
        filterChain.doFilter(request,response);
    }
}