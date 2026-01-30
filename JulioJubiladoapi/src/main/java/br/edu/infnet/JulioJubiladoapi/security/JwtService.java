package br.edu.infnet.JulioJubiladoapi.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String issuer;
    private final int expirationMinutes;
    private final SecretKey signingKey;

    public JwtService(
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-minutes}") int expirationMinutes,
            @Value("${security.jwt.secret}") String secret
    ) {
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        if (!hasText(subject)) {
            throw new IllegalArgumentException("Subject is required");
        }
        Instant now = Instant.now();
        Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        Map<String, Object> safeClaims = claims == null ? Map.of() : claims;

        return Jwts.builder()
                .claims(safeClaims)
                .issuer(issuer)
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public boolean isValid(String token) {
        Claims claims = tryParseClaims(token);
        return claims != null && hasText(claims.getSubject());
    }

    public Claims tryParseClaims(String token) {
        try {
            return parseClaims(token);
        } catch (Exception ex) {
            return null;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
