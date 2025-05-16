package com.example.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(this.secretKeyString);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("Chave JWT para validação (Gateway) inicializada com sucesso a partir de Base64.");
        } catch (IllegalArgumentException e) {
            logger.warn("AVISO (Gateway JwtUtil): jwt.secret não parece ser Base64. Tentando UTF-8. Recomenda-se Base64.", e);
            this.key = Keys.hmacShaKeyFor(this.secretKeyString.getBytes(StandardCharsets.UTF_8));
        }  catch (Exception e) {
            logger.error("!!! ERRO CRÍTICO (Gateway JwtUtil): Falha ao inicializar chave JWT. Verifique a propriedade jwt.secret !!!", e);
            throw new RuntimeException("Falha ao inicializar chave JWT no Gateway", e);
        }
    }

    public Claims validateTokenAndGetClaims(String token) throws ExpiredJwtException, SignatureException, JwtException {
        logger.trace("Validando token: {}", token);
        Jws<Claims> jwsClaims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        logger.trace("Token validado com sucesso. Claims: {}", jwsClaims.getPayload());
        return jwsClaims.getPayload();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public String getUserIdFromClaims(Claims claims) {
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromClaims(Claims claims) {
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List) {
            if (!((List<?>) rolesObject).isEmpty() && ((List<?>) rolesObject).get(0) instanceof String) {
                return (List<String>) rolesObject;
            }
        } else if (rolesObject instanceof String) {
            return List.of((String) rolesObject);
        }
        logger.warn("Claim 'roles' não encontrada ou formato inesperado nas claims.");
        return List.of();
    }
}
