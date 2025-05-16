package com.example.pet_service.config;

import com.example.pet_service.config.filter.UserContextHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
        http.addFilterBefore(userContextHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        http
            .cors(cors -> cors.disable()) // genrenciado pelo api_gateway            
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pets/disponiveis", "/api/pets/{id}").permitAll()
                .requestMatchers("/pet-swagger-ui.html", "/pet-api-docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/pets").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/pets/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
