package com.naeggeodo.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilter");
       // String token = request.getHeader("Authorization");
        //System.out.println("token : " + token);

        //토큰검증 로직 추가하면됨
        /*
        *    토큰 검증로직 추가하여 성공시 doFilter 호출
        *    실패시 exception throw 해서 entrypoint 에서 핸들링할것        * */
        filterChain.doFilter(request,response);
    }
}
