package br.edu.infnet.juliopedidoapi.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.juliopedidoapi.model.domain.exceptions.UnauthorizedException;
import br.edu.infnet.juliopedidoapi.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
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

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
}
