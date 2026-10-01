package com.brainspark.nursepulse.platform.shared.infrastructure.notifications.brevo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BrevoEmailNotificationServiceTest {

    private static final String LINK = "https://backend-nursepulse-qfct.onrender.com/api/v1/authentication/verify-email?token=abc";

    @Test
    void shouldHandleNullOrBlankEmailGracefully() {
        var service = new BrevoEmailNotificationService("dummy-key", "sender@example.com");

        assertDoesNotThrow(() -> service.sendVerificationEmail(null, "John", LINK));
        assertDoesNotThrow(() -> service.sendVerificationEmail("", "John", LINK));
        assertDoesNotThrow(() -> service.sendVerificationEmail("   ", "John", LINK));
    }

    @Test
    void shouldHandleMissingSenderGracefully() {
        var service = new BrevoEmailNotificationService("dummy-key", "");

        assertDoesNotThrow(() -> service.sendVerificationEmail("user@example.com", "John", LINK));
    }

    @Test
    void shouldNotThrowWhenBrevoFails() {
        // With an invalid key, the service should catch the exception and not throw
        var service = new BrevoEmailNotificationService("invalid-key", "sender@example.com");

        assertDoesNotThrow(() -> service.sendVerificationEmail("user@example.com", "John", LINK));
    }
}
