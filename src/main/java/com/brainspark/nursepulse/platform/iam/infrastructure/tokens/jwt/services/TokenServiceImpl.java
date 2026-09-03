package com.brainspark.nursepulse.platform.iam.infrastructure.tokens.jwt.services;

import com.brainspark.nursepulse.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * Token service implementation for JWT tokens.
 * This class is responsible for generating and validating JWT tokens.
 * It uses the secret and expiration days from the application.properties file.
 */
@Service
@Slf4j
public class TokenServiceImpl implements BearerTokenService {
    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private static final int TOKEN_BEGIN_INDEX = 7;


    private final String secret;

    private final int expirationDays;

    public TokenServiceImpl(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days}") int expirationDays
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("authorization.jwt.secret must contain at least 32 bytes");
        }
        if (expirationDays <= 0) {
            throw new IllegalArgumentException("authorization.jwt.expiration.days must be greater than zero");
        }
        this.secret = secret;
        this.expirationDays = expirationDays;
    }

    /**
     * This method generates a JWT token from an authentication object
     * @param authentication the authentication object
     * @return String the JWT token
     * @see Authentication
     */
    @Override
    public String generateToken(Authentication authentication) {
        return buildTokenWithDefaultParameters(authentication.getName());
    }

    /**
     * This method generates a JWT token from a username
     * @param username the username
     * @return String the JWT token
     */
    public String generateToken(String username) {
        return buildTokenWithDefaultParameters(username);
    }

    /**
     * This method generates a JWT token from a username and a secret.
     * It uses the default expiration days from the application.properties file.
     * @param username the username
     * @return String the JWT token
     */
    private String buildTokenWithDefaultParameters(String username) {
        var issuedAt = new Date();
        var expiration = DateUtils.addDays(issuedAt, expirationDays);
        var key = getSigningKey();
        return Jwts.builder()
                .subject(username)
                .issuer("nursepulse-api")
                .id(UUID.randomUUID().toString())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    /**
     * This method extracts the username from a JWT token
     * @param token the token
     * @return String the username
     */
    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * This method validates a JWT token
     * @param token the token
     * @return boolean true if the token is valid, false otherwise
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer("nursepulse-api")
                    .build()
                    .parseSignedClaims(token);
            return true;
        }  catch (SignatureException e) {
            log.debug("Invalid JSON Web Token Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("Invalid JSON Web Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.debug("JSON Web Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("JSON Web Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("JSON Web Token claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extract a claim from a token
     * @param token the token
     * @param claimsResolvers the claims resolver
     * @param <T> the type of the claim
     * @return T the claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Extract all claims from a token
     * @param token the token
     * @return Claims the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer("nursepulse-api")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get the signing key
     * @return SecretKey the signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenPresentIn(String authorizationParameter) {
        return StringUtils.hasText(authorizationParameter);
    }

    private boolean isBearerTokenIn(String authorizationParameter) {
        return authorizationParameter.startsWith(BEARER_TOKEN_PREFIX);
    }

    private String extractTokenFrom(String authorizationHeaderParameter) {
        return authorizationHeaderParameter.substring(TOKEN_BEGIN_INDEX);
    }

    private String getAuthorizationParameterFrom(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_PARAMETER_NAME);
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        String parameter = getAuthorizationParameterFrom(request);
        if (isTokenPresentIn(parameter) && isBearerTokenIn(parameter)) return extractTokenFrom(parameter);
        return null;
    }

}
