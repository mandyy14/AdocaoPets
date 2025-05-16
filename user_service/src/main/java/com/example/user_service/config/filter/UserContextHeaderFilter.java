package com.example.user_service.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserContextHeaderFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserContextHeaderFilter.class);

    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_USER_ROLES = "X-User-Roles";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);
        String rolesString = request.getHeader(HEADER_USER_ROLES);

        // Só tenta criar Authentication se o ID do usuário tiver passado pelo gateway
        if (userId != null && !userId.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Header X-User-ID encontrado: {}. Roles: {}", userId, rolesString);
            try {
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                if (rolesString != null && !rolesString.isEmpty()) {
                    authorities = Arrays.stream(rolesString.split(","))
                                        .map(String::trim)
                                        .filter(role -> !role.isEmpty())
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication object set no SecurityContext para userId: {}", userId);

            } catch (Exception e) {
                logger.error("Erro ao processar headers de usuário ou criar Authentication: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else if (userId == null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Se não tem header e não tem auth, loga
            logger.trace("Nenhum X-User-ID header encontrado e nenhum Authentication no contexto para: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
