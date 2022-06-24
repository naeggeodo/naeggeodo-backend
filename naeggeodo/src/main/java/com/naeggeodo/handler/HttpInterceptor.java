package com.naeggeodo.handler;

import com.naeggeodo.exception.CustomHttpException;
import com.naeggeodo.exception.ErrorCode;
import com.naeggeodo.jwt.AuthorizationExtractor;
import com.naeggeodo.jwt.JwtTokenProvider;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

public class HttpInterceptor implements HandlerInterceptor {

    private JwtTokenProvider jwtTokenProvider;

    public HttpInterceptor(JwtTokenProvider jwtTokenProvider){this.jwtTokenProvider = jwtTokenProvider;}

    //컨트롤러 도달전에 호출됨
    // 추가 검증 로직(토큰.subject와 pathParam의 user_id 비교) 실행
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = AuthorizationExtractor.extract(request);

        Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String user_id = parseUserId(pathVariables);
        String subject = parseSubject(request);

        if(user_id == null || subject == null)
            throw new CustomHttpException(ErrorCode.UNAUTHORIZED);

        if(!user_id.equals(subject))
            throw new CustomHttpException(ErrorCode.UNAUTHORIZED);

        return true;
    }

    private String parseUserId(Map<String, String> pathVariables){

        if(pathVariables != null){
            if(pathVariables.get("user_id") != null){
                return pathVariables.get("user_id");
            }
            return null;
        }
        return null;
    }

    private String parseSubject(HttpServletRequest request){
        String token = AuthorizationExtractor.extract(request);

        if(Objects.isNull(token)) return null;

        return jwtTokenProvider.getSubject(token);
    }

}
