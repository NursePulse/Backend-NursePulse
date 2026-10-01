package com.brainspark.nursepulse.platform.shared.infrastructure.notifications.twilio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TwilioSmsNotificationServiceTest {

    @Test
    void shouldHandleNullOrBlankPhoneGracefully() {
        var service = new TwilioSmsNotificationService("dummy-sid", "dummy-token", "+17372508034");

        assertDoesNotThrow(() -> service.sendCriticalAlertSms(null, "Test alert"));
        assertDoesNotThrow(() -> service.sendCriticalAlertSms("", "Test alert"));
        assertDoesNotThrow(() -> service.sendCriticalAlertSms("   ", "Test alert"));
    }

    @Test
    void shouldHandleMissingFromNumberGracefully() {
        var service = new TwilioSmsNotificationService("dummy-sid", "dummy-token", "");

        assertDoesNotThrow(() -> service.sendCriticalAlertSms("987654321", "Test alert"));
    }

    @Test
    void shouldNotThrowWhenTwilioFails() {
        // With dummy credentials, any call failure is caught by try/catch and logged
        var service = new TwilioSmsNotificationService("invalid-sid", "invalid-token", "+17372508034");

        assertDoesNotThrow(() -> service.sendCriticalAlertSms("987654321", "Test alert"));
        assertDoesNotThrow(() -> service.sendCriticalAlertSms("+51987654321", "Test alert"));
    }
}
