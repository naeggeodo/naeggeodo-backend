package com.naeggeodo.filter;


import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.exception.MyAuthenticationException;
import com.naeggeodo.exception.UnauthorizedException;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
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
			setResponse(response,ErrorCode.UNAUTHORIZED,493);
		}

		filterChain.doFilter(request,response);
	}
	private void setResponse(HttpServletResponse response, ErrorCode code,int status) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		JSONObject responseJson = new JSONObject();
		response.setStatus(status);
		responseJson.put("timestamp", LocalDateTime.now());
		responseJson.put("message", code.getDetail());
		responseJson.put("error", code);

		response.getWriter().print(responseJson);
	}
}

