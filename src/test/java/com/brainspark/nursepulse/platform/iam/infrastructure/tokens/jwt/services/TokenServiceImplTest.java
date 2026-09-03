package com.brainspark.nursepulse.platform.iam.infrastructure.tokens.jwt.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceImplTest {
    private static final String SECRET =
            "nurse-pulse-test-secret-key-with-at-least-thirty-two-bytes";

    @Test
    void shouldGenerateAndValidateToken() {
        var service = new TokenServiceImpl(SECRET, 7);

        var token = service.generateToken("nurse.maria");

        assertTrue(service.validateToken(token));
        assertEquals("nurse.maria", service.getUsernameFromToken(token));
    }

    @Test
    void shouldRejectMalformedToken() {
        var service = new TokenServiceImpl(SECRET, 7);

        assertFalse(service.validateToken("not-a-jwt"));
    }

    @Test
    void shouldRejectWeakSecret() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TokenServiceImpl("too-short", 7)
        );
    }
}
