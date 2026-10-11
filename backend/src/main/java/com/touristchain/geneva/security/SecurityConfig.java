package com.touristchain.geneva.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private static final String[] PUBLIC_CATALOG = {
            "/api/v1/kyoto/destinations/**", "/api/v1/kyoto/hotels/**", "/api/v1/kyoto/cars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, ObjectMapper objectMapper)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_CATALOG).permitAll()
                        .requestMatchers(PUBLIC_CATALOG).hasRole("ADMIN")
                        .requestMatchers("/api/v1/kyoto/companies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews", "/api/v1/reviews/stats",
                                "/api/v1/reviews/partner/**", "/api/v1/reviews/package/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/partners").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/partners/me").hasAnyRole("SOCIO", "ADMIN")
                        .requestMatchers("/api/v1/partners/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tourists/me/loyalty-points").hasRole("ADMIN")
                        .requestMatchers("/api/v1/tourists/me", "/api/v1/tourists/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tourists").authenticated()
                        .requestMatchers("/api/v1/tourists/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/administrators/me", "/api/v1/administrators/me/**").authenticated()
                        .requestMatchers("/api/v1/administrators/**").hasRole("ADMIN")
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> writeError(res, objectMapper,
                                HttpServletResponse.SC_UNAUTHORIZED, "Acceso denegado. Token no proporcionado o inválido."))
                        .accessDeniedHandler((req, res, ex) -> writeError(res, objectMapper,
                                HttpServletResponse.SC_FORBIDDEN, "Acceso denegado. Rol no autorizado.")))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private static void writeError(HttpServletResponse res, ObjectMapper mapper, int status, String message)
            throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        mapper.writeValue(res.getOutputStream(), Map.of("error", message));
    }
}
