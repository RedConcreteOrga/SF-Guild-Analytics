package com.sf.guildanalytics.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    // SECURITY: Minimum 32 bytes (256 bits) required for HS256.
    // In production, set JWT_SECRET environment variable to a base64-encoded random key.
    // Generate with: openssl rand -base64 64
    @Value("${app.jwtSecret:DefaultSecretKeyForJwtOverTwentyFourCharactersLong_CHANGE_IN_PRODUCTION}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes;
        // Support both plain and base64-encoded secrets
        try {
            keyBytes = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            keyBytes = jwtSecret.getBytes();
        }

        // SECURITY: Enforce minimum key length for HS256 (32 bytes = 256 bits)
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                "JWT secret key must be at least 32 bytes (256 bits) for HS256. " +
                "Generate a secure key with: openssl rand -base64 64"
            );
        }

        // Warn in development if still using the default key
        if (jwtSecret.contains("CHANGE_IN_PRODUCTION")) {
            log.warn("SECURITY WARNING: Using default JWT secret. " +
                     "Set the JWT_SECRET environment variable in production.");
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // HS256 is acceptable; upgrade to RS256 if you need token verification without sharing the secret
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported");
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed");
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.warn("JWT token signature validation failed");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token claims string is empty");
        }
        // SECURITY: Never log the token value itself — it is a bearer credential
        return false;
    }
}
