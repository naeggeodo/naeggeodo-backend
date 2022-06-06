package com.naeggeodo.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret-key}")
	private String secretKey;
//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.access-token.expire-length}")//24시간
    private long accessTokenExpiredInMilliseconds;
    @Value("${jwt.refresh-token.expire-length}")//7일
    private long refreshTokenExpiredInMilliseconds;

    public String createToken(String subject) {


    	LoggerFactory.getLogger(this.getClass()).info(secretKey);
        if (accessTokenExpiredInMilliseconds <= 0) {
            throw new RuntimeException("Expiry time must be greater than Zero : ["+accessTokenExpiredInMilliseconds+"] ");

        }
        
        Claims claims = Jwts.claims().setSubject(subject);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime()+accessTokenExpiredInMilliseconds))
                .setIssuer("naeggeodo.com")
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, secretKey);


        return builder.compact();
    }
 
    public String createRefreshToken(String subject) {
    	Claims claims = Jwts.claims().setSubject(subject);
    	
    	return Jwts.builder()
    			.setClaims(claims)
    			.setExpiration(new Date(new Date().getTime()+refreshTokenExpiredInMilliseconds))
    			.signWith(SignatureAlgorithm.HS256, secretKey)
    			.compact();
    }

    //대상 조회
    public String getSubject(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    
    //확인필요
    public Claims getTokenData(String token) {
    	Claims claims = Jwts.parserBuilder()
    			.setSigningKey(secretKey)
    			.build()
    			.parseClaimsJws(token).getBody();
    	return claims;
    }
    

    //유효토근 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
}