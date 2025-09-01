package com.bci.service.user.service;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio responsable de generar y validar tokens JWT para la autenticación de usuarios.
 * <p>
 * Utiliza la biblioteca {@code jjwt} para crear tokens firmados con algoritmo HS256.
 * La clave secreta se define en el archivo de configuración como {@code jwt.secret} (Base64).
 * </p>
 *
 * <p>Configuración esperada en {@code application.properties} o {@code application.yml}:</p>
 *
 * <pre>
 * jwt.secret=TU_CLAVE_EN_BASE64
 * jwt.expiration.hours=24
 * jwt.issuer=user-api
 * </pre>
 *
 * @author Nataly Otero Celis
 * @version 1.0
 * @since 2025-08-27
 */

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration.hours:24}")
    private int expirationHours;

    @Value("${jwt.issuer:user-api}")
    private String issuer;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {

        this.secretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretString));;
        logger.info("JWT Service initialized with expiration: {} hours", expirationHours);
    }

    /**
     * Genera un token JWT para el usuario
     * @param email Email del usuario
     * @param userId ID del usuario
     * @param additionalClaims Claims adicionales opcionales
     * @return Token JWT
     */
    public String generateToken(String email, UUID userId, Map<String, Object> additionalClaims) {
        var now = Instant.now();
        var expiration = now.plus(expirationHours, ChronoUnit.HOURS);

        var claimsBuilder = Jwts.claims()
                .subject(email)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .add("userId", userId.toString())
                .add("type", "access_token");

        // Agregar claims adicionales si existen
        if (additionalClaims != null && !additionalClaims.isEmpty()) {
            additionalClaims.forEach(claimsBuilder::add);
        }

        try {
            String token = Jwts.builder()
                    .claims(claimsBuilder.build())
                    .signWith(secretKey,Jwts.SIG.HS256)
                    .compact();

            logger.debug("Generated JWT token for user: {}", email);
            return token;

        } catch (Exception e) {
            logger.error("Error generating JWT token for user: {}", email, e);
            throw new RuntimeException("Error al generar token JWT", e);
        }
    }



    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
