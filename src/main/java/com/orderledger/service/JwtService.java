package com.orderledger.service;

import com.orderledger.util.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

    @Value(("${ACCESS_TOKEN_EXPIRATION}"))
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${REFRESH_TOKEN_EXPIRATION}")
    private long REFRESH_TOKEN_EXPIRATION;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateAccessToken(String email,Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "acceAss");
        claims.put("role",role.name());
        return buildToken(claims, email, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        claims.put("role",role.name());
        return buildToken(claims, email, REFRESH_TOKEN_EXPIRATION);
    }
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}