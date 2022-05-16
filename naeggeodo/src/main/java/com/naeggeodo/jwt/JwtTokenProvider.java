package com.naeggeodo.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    Logger logger = LoggerFactory.getLogger(this.getClass());
    long ttlMillis = (1000 * 60L * 60L * 24L); //유효시간 24시간

    public String createToken(String subject) {
    	
        if (ttlMillis <= 0) {
            throw new RuntimeException("Expiry time must be greater than Zero : ["+ttlMillis+"] ");
        }

        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .setIssuer("naeggeodo.com")
                .setHeaderParam("typ", "JWT")
                .signWith(SECRET_KEY);

        long nowMillis = System.currentTimeMillis();
        builder.setExpiration(new Date(nowMillis + ttlMillis));
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
        Date now = new Date();
        long validity = System.currentTimeMillis() + ttlMillis;
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(validity))
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