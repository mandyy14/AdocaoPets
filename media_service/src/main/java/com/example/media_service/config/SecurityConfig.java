package com.example.media_service.config;

import com.example.media_service.config.filter.UserContextHeaderFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserContextHeaderFilter userContextHeaderFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable()) // genrenciado pelo api_gateway
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/media/serve/**").permitAll()
                .requestMatchers("/api/media/media-swagger-ui.html", "/api/media/media-api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/media/profile-picture-info/{userId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/media/upload/profile-picture/{userId}").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/media/upload/pet-picture/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(userContextHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
