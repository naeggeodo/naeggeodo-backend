package com.naeggeodo.config;

import com.naeggeodo.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        String[] ignoreURL = {
                "/login/OAuth/**",
                "/oauth/getInfo/**",
                "/chat-rooms",
                "/categories",
                "/chat/**"
        };
        web.ignoring().antMatchers(HttpMethod.GET,ignoreURL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().formLogin().disable().httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
               //.exceptionHandling()
                //.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;


    }
}
