package com.example.api_gateway.filter;

import com.example.api_gateway.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/api/users/login",
            "/api/users/cadastrar",
            "/api/media/serve",
            "/swagger-ui.html", 
            "/swagger-ui/",
            "/v3/api-docs",
            "/api/users/user-api-docs",
            "/api/media/media-api-docs",
            "/api/pets/pet-api-docs"
    );

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> headerMap = new HashMap<>();

        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void addHeader(String name, String value) {
            this.headerMap.put(name.toLowerCase(), value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = this.headerMap.get(name.toLowerCase());
            if (headerValue != null) {
                return headerValue;
            }
            return ((HttpServletRequest) getRequest()).getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(this.headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String headerValue = this.headerMap.get(name.toLowerCase());
            if (headerValue != null) {
                return Collections.enumeration(Collections.singletonList(headerValue));
            }
            return super.getHeaders(name);
        }
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("AuthFilter (Gateway): Recebido {} para {}", method, path);

        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(method) ||
            PUBLIC_PATH_PREFIXES.stream().anyMatch(prefix -> path.startsWith(prefix))) {
            log.debug("AuthFilter: Requisição OPTIONS ou Path público: {}, permitindo passagem.", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            log.warn("AuthFilter: Header 'Authorization' ausente ou mal formatado para path: {}", path);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token de autorização ausente ou inválido.");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);
            String userId = jwtUtil.getUserIdFromClaims(claims);
            List<String> rolesList = jwtUtil.getRolesFromClaims(claims);

            if (userId == null || userId.isEmpty()) {
                log.warn("AuthFilter: Token válido mas não contém 'subject' (userId).");
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token inválido (sem ID de usuário).");
                return;
            }

            log.info("AuthFilter: Token validado para userId: {}, roles: {}", userId, rolesList);

            List<SimpleGrantedAuthority> authorities = rolesList.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("AuthFilter: Contexto de segurança do Gateway populado para userId: {}", userId);

            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
            requestWrapper.addHeader("X-User-ID", userId);
            requestWrapper.addHeader("X-User-Roles", String.join(",", rolesList));
            log.debug("AuthFilter: Adicionando headers X-User-ID: {} e X-User-Roles: {} para downstream.", userId, String.join(",", rolesList));

            filterChain.doFilter(requestWrapper, response);

        } catch (ExpiredJwtException eje) {
            log.warn("AuthFilter: Token JWT expirado para {}: {}", path, eje.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token expirado.");
        } catch (SignatureException se) {
            log.warn("AuthFilter: Assinatura do token JWT inválida para {}: {}", path, se.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Assinatura do token inválida.");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("AuthFilter: Token JWT inválido ou mal formatado para {}: {}", path, e.getMessage());
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token inválido ou mal formatado.");
        } catch (Exception e) {
            log.error("AuthFilter: Erro inesperado para {}: {}", path, e.getMessage(), e);
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no gateway durante autenticação.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", java.time.Instant.now().toString());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
