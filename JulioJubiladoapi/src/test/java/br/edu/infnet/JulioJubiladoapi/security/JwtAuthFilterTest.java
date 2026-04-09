package br.edu.infnet.JulioJubiladoapi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class JwtAuthFilterTest {

    private JwtService jwtService;
    private TokenBlacklistService tokenBlacklistService;
    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = Mockito.mock(JwtService.class);
        tokenBlacklistService = Mockito.mock(TokenBlacklistService.class);
        filter = new JwtAuthFilter(jwtService, tokenBlacklistService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void passesThroughWithoutAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        Mockito.verify(chain).doFilter(Mockito.any(), Mockito.any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rejectsInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        Mockito.when(jwtService.tryParseClaims("invalid")).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        Mockito.verify(chain, Mockito.never()).doFilter(Mockito.any(), Mockito.any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rejectsRevokedToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer revoked");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        Claims claims = Jwts.claims()
                .subject("user")
                .id("token-1")
                .add("role", "ADMIN")
                .build();

        Mockito.when(jwtService.tryParseClaims("revoked")).thenReturn(claims);
        Mockito.when(tokenBlacklistService.isRevoked("token-1")).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        Mockito.verify(chain, Mockito.never()).doFilter(Mockito.any(), Mockito.any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void authenticatesValidTokenAndContinues() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        Claims claims = Jwts.claims()
                .subject("user")
                .id("token-2")
                .add("role", "ADMIN")
                .build();

        Mockito.when(jwtService.tryParseClaims("valid")).thenReturn(claims);
        Mockito.when(tokenBlacklistService.isRevoked("token-2")).thenReturn(false);

        filter.doFilter(request, response, chain);

        Mockito.verify(chain).doFilter(Mockito.any(), Mockito.any());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("user", authentication.getName());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
