package br.edu.infnet.JulioJubiladoapi.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class TokenBlacklistServiceTest {

    @Test
    void revokeIgnoresNullOrBlankToken() {
        TokenBlacklistService service = new TokenBlacklistService();

        service.revoke(null, Instant.now().plusSeconds(60));
        service.revoke("   ", Instant.now().plusSeconds(60));

        assertFalse(service.isRevoked(null));
        assertFalse(service.isRevoked("   "));
    }

    @Test
    void revokedTokenIsBlockedUntilExpiry() {
        TokenBlacklistService service = new TokenBlacklistService();
        Instant expiresAt = Instant.now().plusSeconds(30);

        service.revoke("token-1", expiresAt);

        assertTrue(service.isRevoked("token-1"));
    }

    @Test
    void expiredTokenIsCleared() {
        TokenBlacklistService service = new TokenBlacklistService();
        Instant expiresAt = Instant.now().minusSeconds(1);

        service.revoke("token-2", expiresAt);

        assertFalse(service.isRevoked("token-2"));
        assertFalse(service.isRevoked("token-2"));
    }
}
