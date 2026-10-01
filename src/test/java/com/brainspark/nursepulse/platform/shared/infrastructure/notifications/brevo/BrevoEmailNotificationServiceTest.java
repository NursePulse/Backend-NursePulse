package com.brainspark.nursepulse.platform.shared.infrastructure.notifications.brevo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BrevoEmailNotificationServiceTest {

    @Test
    void shouldHandleNullOrBlankEmailGracefully() {
        var service = new BrevoEmailNotificationService("dummy-key", "sender@example.com");

        assertDoesNotThrow(() -> service.sendWelcomeEmail(null, "John"));
        assertDoesNotThrow(() -> service.sendWelcomeEmail("", "John"));
        assertDoesNotThrow(() -> service.sendWelcomeEmail("   ", "John"));
    }

    @Test
    void shouldHandleMissingSenderGracefully() {
        var service = new BrevoEmailNotificationService("dummy-key", "");

        assertDoesNotThrow(() -> service.sendWelcomeEmail("user@example.com", "John"));
    }

    @Test
    void shouldNotThrowWhenBrevoFails() {
        // With an invalid key, the service should catch the exception and not throw
        var service = new BrevoEmailNotificationService("invalid-key", "sender@example.com");

        assertDoesNotThrow(() -> service.sendWelcomeEmail("user@example.com", "John"));
    }
}
