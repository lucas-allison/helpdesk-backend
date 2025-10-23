package com.lucas.helpdesk.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private final SecretKey signingKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean tokenValido(String token) {
        Claims claims = getClaims(token);

        if (claims != null) {
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            return username != null && expirationDate != null && now.before(expirationDate);
        }
        return false;
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}