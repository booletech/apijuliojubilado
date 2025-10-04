package br.edu.infnet.mono.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // ============ H2 Console (apenas desenvolvimento) ============
                .requestMatchers("/h2-console/**").permitAll()
                
                // ============ CLIENTES ============
                // GET e POST: ADMIN e USER
                .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/*").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/clientes/*").hasAnyRole("ADMIN", "USER")
                
                // DELETE e PATCH: apenas ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/clientes/*/fiado").hasRole("ADMIN")
                
                // ============ FUNCIONARIOS ============
                // GET por ID e POST: ADMIN e USER
                .requestMatchers(HttpMethod.GET, "/api/funcionarios/*").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/funcionarios").hasAnyRole("ADMIN", "USER")
                
                // GET lista, PUT, DELETE, PATCH: apenas ADMIN
                .requestMatchers(HttpMethod.GET, "/api/funcionarios").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/funcionarios/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/funcionarios/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/funcionarios/*/inativar").hasRole("ADMIN")
                
                // ============ TICKETSTAREFA ============
                // GET e POST: ADMIN e USER
                .requestMatchers(HttpMethod.GET, "/api/tickets", "/api/tickets/*").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("ADMIN", "USER")
                
                // PUT e DELETE: apenas ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/tickets/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/tickets/*").hasRole("ADMIN")
                
                // ============ TAREFAS ============
                // GET e POST: ADMIN e USER
                .requestMatchers(HttpMethod.GET, "/api/tarefas", "/api/tarefas/*").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/tarefas").hasAnyRole("ADMIN", "USER")
                
                // PUT e DELETE: apenas ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/tarefas/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/tarefas/*").hasRole("ADMIN")
                
                // ============ SEGURANÇA POR PADRÃO ============
                // Qualquer endpoint não mapeado requer autenticação
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults());
        
        // H2 Console frames
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("userPass"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}