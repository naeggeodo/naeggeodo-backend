package com.naeggeodo.config;

import com.naeggeodo.handler.HttpInterceptor;
import com.naeggeodo.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("https://localhost:8080","https://naeggeodo.com").allowedMethods("*")
				.allowCredentials(true);
	}

	//컨트롤러에 도달하기 전에 호출될 인터셉터를 지정함
	// addPathPatterns() 로 특정 url 패턴 지정
	// Path에 user_id 포함된 요청에 대해서 추가검증함

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
				.addInterceptor(new HttpInterceptor(jwtTokenProvider))
				.addPathPatterns(
						"/user/**",
						"/chat-rooms/{chatMain_id}/users/**",
						"/chat-rooms/progressing/user/**",
						"/chat-rooms/user/**",
						"/chat-rooms/{chatMain_id}/bookmarks/**",
						"/chat-rooms/{chatMain_id}/bookmarks/**",
						"/chat-rooms/order-list/**"
				)
		;
	}
}
