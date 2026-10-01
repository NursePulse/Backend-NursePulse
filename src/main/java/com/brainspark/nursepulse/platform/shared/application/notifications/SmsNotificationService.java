package com.brainspark.nursepulse.platform.shared.application.notifications;

public interface SmsNotificationService {
    void sendCriticalAlertSms(String toPhone, String message);
}
