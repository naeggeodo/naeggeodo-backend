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
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${jwt.access-token.expire-length}")//24시간
    private long accessTokenExpiredInMilliseconds;
    @Value("${jwt.refresh-token.expire-length}")//7일
    private long refreshTokenExpiredInMilliseconds;

    public String createToken(String subject) {
    	
        if (accessTokenExpiredInMilliseconds <= 0) {
            throw new RuntimeException("Expiry time must be greater than Zero : ["+accessTokenExpiredInMilliseconds+"] ");
        }

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer("naeggeodo.com")
                .setHeaderParam("typ", "JWT")
                .signWith(SECRET_KEY);

        builder.setExpiration(new Date(accessTokenExpiredInMilliseconds));
        return builder.compact();
    }
 
    public static String getSubject(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    
    public static Claims getTokenData(String token) {
    	Claims claims = Jwts.parserBuilder()
    			.setSigningKey(SECRET_KEY)
    			.build()
    			.parseClaimsJws(token).getBody();
    	return claims;
    }
    

    public String createRefreshToken(String payload) {
        Claims claims = Jwts.claims().setSubject(payload);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(refreshTokenExpiredInMilliseconds))
                .signWith(SECRET_KEY)
                .compact();
       	}
 
    //유효토근 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}