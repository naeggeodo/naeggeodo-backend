package com.naeggeodo.filter;


import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private JwtTokenProvider jwtProvider;
	private static int count = 0;

	public JwtAuthenticationFilter(JwtTokenProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String token = AuthorizationExtractor.extract(request);
		log.info("=================count"+count+++"======================");
		log.info("out of if token = {}",token);
		log.info("requestURL = {}",request.getRequestURL());
		log.info("=======================================================");
		if(Objects.isNull(token)) {
			log.info("======sendError 499");
			setResponse(response,ErrorCode.UNAUTHORIZED_NULL,499);
			return;
		}
		if(!jwtProvider.validateToken(token)) {
			log.info("======sendError 401");
			setResponse(response,ErrorCode.UNAUTHORIZED,401);
			return;
		}

		filterChain.doFilter(request,response);
	}
	private void setResponse(HttpServletResponse response, ErrorCode code,int status) throws IOException {
		response.setContentType("application/json;charset=UTF-8");

		JSONObject responseJson = new JSONObject();
		response.setStatus(status);
		responseJson.put("timestamp", LocalDateTime.now());
		responseJson.put("message", code.getDetail());
		responseJson.put("error", code);

		response.getWriter().print(responseJson);
	}
}

