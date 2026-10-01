package com.brainspark.nursepulse.platform.shared.application.notifications;

public interface EmailNotificationService {
    void sendVerificationEmail(String toEmail, String firstName, String verificationLink);
}
