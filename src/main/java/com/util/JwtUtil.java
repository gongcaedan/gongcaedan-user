package com.util;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public JwtUtil(@Value("${jwt.token.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);  // Base64 decode 추가
        secretKey = new SecretKeySpec(keyBytes,
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseClaimsJws(token).getPayload().get("email", String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseClaimsJws(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseClaimsJws(token).getPayload().getExpiration().before(new Date());
    }

    public String createToken(String email, String role, String category){
        long exp = category.equals("access") ? accessExpiration : refreshExpiration;

        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(secretKey)
                .compact();
    }
}