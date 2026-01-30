package br.edu.infnet.JulioJubiladoapi.auth;

import java.util.Map;

import br.edu.infnet.JulioJubiladoapi.model.domain.exceptions.UnauthorizedException;
import br.edu.infnet.JulioJubiladoapi.security.JwtService;
import br.edu.infnet.JulioJubiladoapi.security.TokenBlacklistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final TokenBlacklistService tokenBlacklistService;

	public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, TokenBlacklistService tokenBlacklistService) {
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
		Authentication auth;
		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(req.username(), req.password())
			);
		} catch (AuthenticationException ex) {
			throw new UnauthorizedException("Credenciais invalidas");
		}

		String role = auth.getAuthorities().stream()
				.findFirst()
				.map(granted -> granted.getAuthority())
				.orElse("ROLE_ADMIN");
		String token = jwtService.generateToken(auth.getName(), Map.of("role", role));
		return ResponseEntity.ok(Map.of(
				"tokenType", "Bearer",
				"accessToken", token
				));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
		if (auth == null || !auth.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Token ausente");
		}

		String token = auth.substring("Bearer ".length()).trim();
		var claims = jwtService.tryParseClaims(token);
		if (claims == null || claims.getId() == null || claims.getExpiration() == null) {
			throw new UnauthorizedException("Token invalido");
		}

		tokenBlacklistService.revoke(claims.getId(), claims.getExpiration().toInstant());
		return ResponseEntity.noContent().build();
	}

	public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
