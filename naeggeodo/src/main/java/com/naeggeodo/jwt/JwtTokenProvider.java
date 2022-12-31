package com.naeggeodo.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    //    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${jwt.access-token.expire-length}")//24시간
    private long accessTokenExpiredInMilliseconds;
    @Value("${jwt.refresh-token.expire-length}")//7일
    private long refreshTokenExpiredInMilliseconds;

    public String createToken(String subject) {


        Claims claims = Jwts.claims().setSubject(subject);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + accessTokenExpiredInMilliseconds))
                .setIssuer("naeggeodo.com")
                .setHeaderParam("typ", "JWT")
                .signWith(SignatureAlgorithm.HS256, secretKey);


        return builder.compact();
    }

    public String createRefreshToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + refreshTokenExpiredInMilliseconds))
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


    //유효토근 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.info("validateToken Throw ={} and return false", e.getClass());
            log.info("token = {}", token);
            return false;
        }
    }

}