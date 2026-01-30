package br.edu.infnet.JulioJubiladoapi.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.edu.infnet.JulioJubiladoapi.model.domain.Funcionario;
import br.edu.infnet.JulioJubiladoapi.model.repository.FuncionarioRepository;

@Configuration
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.headers(h -> h.frameOptions(frame -> frame.disable())) // H2 console
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/login").permitAll()
				.requestMatchers("/auth/logout").authenticated()
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/funcionarios").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMIN")

				.requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")
				.requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")
				.requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.PATCH, "/api/clientes/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasAnyRole("ADMIN", "SUPERVISOR")

				.requestMatchers(HttpMethod.GET, "/api/funcionarios/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.PUT, "/api/funcionarios/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.PATCH, "/api/funcionarios/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.DELETE, "/api/funcionarios/**").hasAnyRole("ADMIN", "SUPERVISOR")

				.requestMatchers(HttpMethod.GET, "/api/tarefas/**").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")
				.requestMatchers(HttpMethod.POST, "/api/tarefas").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")
				.requestMatchers(HttpMethod.PUT, "/api/tarefas/**").hasAnyRole("ADMIN", "SUPERVISOR")
				.requestMatchers(HttpMethod.DELETE, "/api/tarefas/**").hasAnyRole("ADMIN", "SUPERVISOR")

				.requestMatchers(HttpMethod.GET, "/api/tickets/**").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO", "TOTEM")
				.requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")
				.requestMatchers(HttpMethod.PUT, "/api/tickets/**").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO")

				.requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "SUPERVISOR", "FUNCIONARIO", "TOTEM")
				.requestMatchers(HttpMethod.GET, "/api/export/**").hasAnyRole("ADMIN", "SUPERVISOR")

				.requestMatchers("/api/**").authenticated()
				.anyRequest().permitAll()
			)
			.httpBasic(Customizer.withDefaults());

		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public UserDetailsService userDetailsService(FuncionarioRepository repository) {
		return username -> {
			Funcionario funcionario = repository.findByLogin(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
			String perfil = funcionario.getPerfil();
			if (perfil == null || perfil.isBlank()) {
				perfil = "FUNCIONARIO";
			}
			String normalized = perfil.toUpperCase();
			if (normalized.startsWith("ROLE_")) {
				normalized = normalized.substring("ROLE_".length());
			}
			return User.withUsername(funcionario.getLogin())
					.password(funcionario.getSenha())
					.roles(normalized)
					.disabled(!funcionario.isAtivo())
					.build();
		};
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173", "https://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
