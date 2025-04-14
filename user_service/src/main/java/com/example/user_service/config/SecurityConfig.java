package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            .headers(headers -> headers
            .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())
            )

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/users/cadastrar", "/api/users/login").permitAll()

                // --- PERMISSÃO TEMPORÁRIA PARA TESTAR UPLOAD/UPDATE DA URL DA IMAGEM ---
                // Libera o endpoint PATCH que recebe a URL da imagem vinda do frontend após upload no media-service
                .requestMatchers(HttpMethod.PATCH, "/api/users/{id}/profile-image-url").permitAll() // <<< LINHA ADICIONADA/AJUSTADA
                // --- LEMBRE-SE DE REMOVER permitAll() E USAR authenticated() AQUI DEPOIS! ---

                .requestMatchers(HttpMethod.GET, "/api/users/{id}/profile-picture-url").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/users/{id}/email", "/api/users/{id}/password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/listar", "/api/users/buscar/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/deletar/**").permitAll()
                .anyRequest().permitAll()
            )

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
