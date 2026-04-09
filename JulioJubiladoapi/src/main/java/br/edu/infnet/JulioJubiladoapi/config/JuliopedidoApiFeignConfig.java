package br.edu.infnet.JulioJubiladoapi.config;

import java.util.Map;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import br.edu.infnet.JulioJubiladoapi.security.JwtService;

@Configuration
public class JuliopedidoApiFeignConfig {

    private final JwtService jwtService;
    private final String serviceSubject;

    public JuliopedidoApiFeignConfig(
            JwtService jwtService,
            @Value("${juliopedidoapi.service-subject:service-juliopedidoapi}") String serviceSubject
    ) {
        this.jwtService = jwtService;
        this.serviceSubject = serviceSubject;
    }

    @Bean
    public RequestInterceptor juliopedidoAuthInterceptor() {
        return template -> {
            String token = jwtService.generateToken(serviceSubject, Map.of("role", "SERVICE"));
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        };
    }
}
