package br.edu.infnet.JulioJubiladoapi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import io.jsonwebtoken.Claims;

class JwtServiceTest {

    private static final String ISSUER = "JulioApis";
    private static final int EXP_MINUTES = 60;
    private static final String SECRET = "CHAVE_SUPER_SECRETA_JULIO_APIS_COM_NO_MINIMO_32_CARACTERES";

    private final JwtService jwtService = new JwtService(ISSUER, EXP_MINUTES, SECRET);

    @Test
    void generateTokenUsesIssuerAndSubject() {
        String token = jwtService.generateToken("admin", Map.of("role", "ADMIN"));

        Claims claims = jwtService.parseClaims(token);

        assertEquals("admin", claims.getSubject());
        assertEquals(ISSUER, claims.getIssuer());
        assertEquals("ADMIN", claims.get("role"));
        assertTrue(jwtService.isValid(token));
        assertEquals("admin", jwtService.extractSubject(token));
    }

    @Test
    void expirationIsBasedOnIssuedAt() {
        String token = jwtService.generateToken("user", Map.of());

        Claims claims = jwtService.parseClaims(token);
        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant expiresAt = claims.getExpiration().toInstant();

        assertTrue(expiresAt.isAfter(issuedAt));
        assertEquals(EXP_MINUTES, Duration.between(issuedAt, expiresAt).toMinutes());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void generateTokenRejectsBlankSubject(String subject) {
        assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(subject, Map.of()));
    }

    @Test
    void isValidRejectsTokenFromDifferentIssuer() {
        JwtService otherIssuer = new JwtService("OtherIssuer", EXP_MINUTES, SECRET);
        String token = otherIssuer.generateToken("user", Map.of());

        assertFalse(jwtService.isValid(token));
    }

    @Test
    void tryParseClaimsReturnsNullForInvalidToken() {
        Claims claims = jwtService.tryParseClaims("not-a-jwt");

        assertNull(claims);
    }

    @Test
    void parseClaimsReturnsClaimsForValidToken() {
        String token = jwtService.generateToken("user", Map.of("role", "FUNCIONARIO"));

        Claims claims = jwtService.parseClaims(token);

        assertNotNull(claims);
        assertEquals("user", claims.getSubject());
    }
}
