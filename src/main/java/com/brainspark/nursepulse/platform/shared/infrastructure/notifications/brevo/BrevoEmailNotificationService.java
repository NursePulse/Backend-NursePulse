package com.brainspark.nursepulse.platform.shared.infrastructure.notifications.brevo;

import com.brainspark.nursepulse.platform.shared.application.notifications.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class BrevoEmailNotificationService implements EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailNotificationService.class);

    private final RestClient restClient;
    private final String senderEmail;

    public BrevoEmailNotificationService(
            @Value("${brevo.api-key:}") String apiKey,
            @Value("${brevo.sender-email:}") String senderEmail
    ) {
        this.senderEmail = senderEmail;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey != null ? apiKey : "")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        if (toEmail == null || toEmail.isBlank()) {
            log.info("No recipient email provided, skipping welcome email");
            return;
        }

        if (senderEmail == null || senderEmail.isBlank()) {
            log.warn("Brevo sender email is not configured, skipping welcome email to {}", toEmail);
            return;
        }

        try {
            var name = firstName != null ? firstName : "";
            restClient.post()
                    .uri("/smtp/email")
                    .body(Map.of(
                            "sender", Map.of("email", senderEmail, "name", "NursePulse"),
                            "to", java.util.List.of(Map.of("email", toEmail, "name", name)),
                            "subject", "Bienvenido a NursePulse",
                            "htmlContent", "<p>Hola " + name + ", tu cuenta en NursePulse fue creada con exito.</p>"
                    ))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Welcome email sent successfully via Brevo to {}", toEmail);
        } catch (RuntimeException exception) {
            // No bloquear el sign-up si Brevo falla — solo loguear.
            log.warn("Failed to send welcome email via Brevo to {}: {}", toEmail, exception.getMessage());
        }
    }
}
