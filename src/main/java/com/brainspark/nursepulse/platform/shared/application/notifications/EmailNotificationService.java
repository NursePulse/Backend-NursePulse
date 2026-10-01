package com.brainspark.nursepulse.platform.shared.application.notifications;

public interface EmailNotificationService {
    void sendWelcomeEmail(String toEmail, String firstName);
}
