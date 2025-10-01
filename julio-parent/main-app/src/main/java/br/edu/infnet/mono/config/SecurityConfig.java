/*
 * package br.edu.infnet.mono.config;
 * 
 * import static org.springframework.security.config.Customizer.withDefaults;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.http.HttpMethod; import
 * org.springframework.security.config.annotation.method.configuration.
 * EnableMethodSecurity; import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import org.springframework.security.config.annotation.web.configuration.
 * EnableWebSecurity; import org.springframework.security.core.userdetails.User;
 * import org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.security.provisioning.InMemoryUserDetailsManager; import
 * org.springframework.security.web.SecurityFilterChain;
 * 
 * @Configuration
 * 
 * @EnableWebSecurity
 * 
 * @EnableMethodSecurity(prePostEnabled = true) public class SecurityConfig {
 * 
 * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
 * throws Exception { http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
 * authorize -> authorize.requestMatchers("/h2-console/**").permitAll()
 * 
 * .requestMatchers(HttpMethod.GET, "/comentarios").hasAnyRole("ADMIN", "USER")
 * .requestMatchers(HttpMethod.DELETE, "/comentarios/**").hasRole("ADMIN")
 * 
 * .anyRequest().authenticated()) .httpBasic(withDefaults());
 * 
 * http.headers(headers -> headers.frameOptions(frameOptions ->
 * frameOptions.disable()));
 * 
 * return http.build(); }
 * 
 * @Bean public UserDetailsService userDetailsService() { UserDetails admin =
 * User.builder().username("admin").password(passwordEncoder().encode(
 * "adminPass")).roles("ADMIN").build(); UserDetails user =
 * User.builder().username("user").password(passwordEncoder().encode("userPass")
 * ).roles("USER").build();
 * 
 * return new InMemoryUserDetailsManager(admin, user); }
 * 
 * @Bean public PasswordEncoder passwordEncoder() { return new
 * BCryptPasswordEncoder(); } }
 */