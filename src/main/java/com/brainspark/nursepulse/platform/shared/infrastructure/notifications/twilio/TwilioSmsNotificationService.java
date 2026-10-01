package com.brainspark.nursepulse.platform.shared.infrastructure.notifications.twilio;

import com.brainspark.nursepulse.platform.shared.application.notifications.SmsNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Service
public class TwilioSmsNotificationService implements SmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsNotificationService.class);

    private final RestClient restClient;
    private final String fromNumber;

    public TwilioSmsNotificationService(
            @Value("${twilio.account-sid:}") String accountSid,
            @Value("${twilio.auth-token:}") String authToken,
            @Value("${twilio.from-number:}") String fromNumber
    ) {
        this.fromNumber = fromNumber;
        var sid = accountSid != null ? accountSid.trim() : "";
        var token = authToken != null ? authToken.trim() : "";
        var credentials = Base64.getEncoder()
                .encodeToString((sid + ":" + token).getBytes());
        this.restClient = RestClient.builder()
                .baseUrl("https://api.twilio.com/2010-04-01/Accounts/" + sid)
                .defaultHeader("Authorization", "Basic " + credentials)
                .build();
    }

    @Override
    public void sendCriticalAlertSms(String toPhone, String message) {
        if (toPhone == null || toPhone.isBlank()) {
            log.info("No recipient phone provided, skipping critical alert SMS");
            return;
        }

        if (fromNumber == null || fromNumber.isBlank()) {
            log.warn("Twilio from-number is not configured, skipping critical alert SMS to {}", toPhone);
            return;
        }

        try {
            var body = new LinkedMultiValueMap<String, String>();

            String formattedTo = toPhone.trim();
            if (!formattedTo.startsWith("+")) {
                formattedTo = "+51" + formattedTo; // default country code Peru
            }

            String formattedFrom = fromNumber.trim();
            if (!formattedFrom.startsWith("+")) {
                formattedFrom = "+" + formattedFrom;
            }

            body.add("To", formattedTo);
            body.add("From", formattedFrom);
            body.add("Body", message);

            restClient.post()
                    .uri("/Messages.json")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Critical alert SMS sent successfully via Twilio to {}", formattedTo);
        } catch (RuntimeException exception) {
            log.warn("Failed to send SMS via Twilio to {}: {}", toPhone, exception.getMessage());
        }
    }
}
