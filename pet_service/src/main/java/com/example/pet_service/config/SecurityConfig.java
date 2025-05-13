package com.example.pet_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable()) // genrenciado pelo api_gateway            
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // fallback
                .requestMatchers(HttpMethod.GET, "/api/pets/disponiveis", "/api/pets/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/pets").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/pets/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**").authenticated()
                .requestMatchers("/pet-swagger-ui.html", "/pet-api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
