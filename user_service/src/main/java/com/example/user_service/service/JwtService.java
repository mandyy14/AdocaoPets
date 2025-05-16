package com.example.user_service.service;

import com.example.user_service.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("Chave secreta JWT inicializada com sucesso a partir de Base64.");
        } catch (IllegalArgumentException e) {
            logger.warn("AVISO: jwt.secret não parece ser uma string Base64 válida: '{}'. Tentando usar getBytes(UTF-8). É altamente recomendável usar uma chave Base64 segura e longa.", secretKeyString, e);
            this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Gera um token JWT para um usuário autenticado.
     * @param usuario
     * @return String JWT.
     */
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Claims para o payload do token
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", usuario.getEmail());
        claims.put("roles", List.of(usuario.getCargo() != null ? usuario.getCargo().toUpperCase() : "USER"));

        logger.debug("Gerando token para usuário ID: {}, email: {}, roles: {}", usuario.getId(), claims.get("email"), claims.get("roles"));

        return Jwts.builder()
                .claims(claims)
                .subject(Long.toString(usuario.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}
