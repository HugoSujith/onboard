package com.hugo.metalbroker.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {
    private final Key secretKey;

    public JWTUtils() {
        secretKey = new SecretKeySpec(Dotenv.load().get("SECRET_KEY").getBytes(), "HmacSHA256");
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer("Sujith")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }

    public Claims decodeJWTToken(String jwt) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean validate(String token, String username) {
        Claims claims = decodeJWTToken(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis())) && username.equals(claims.getSubject());
    }
}