package com.example.user_service.config;

import com.example.user_service.config.filter.UserContextHeaderFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserContextHeaderFilter userContextHeaderFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(userContextHeaderFilter, AnonymousAuthenticationFilter.class);
        http
            .cors(cors -> cors.disable()) // genrenciado pelo api_gateway
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //fallback
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/users/cadastrar", "/api/users/login").permitAll()
                .requestMatchers("/api/users/user-swagger-ui.html", "/api/users/user-api-docs/**", "/api/users/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/listar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}/deletar").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }  

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
