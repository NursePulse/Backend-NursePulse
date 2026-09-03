package com.brainspark.nursepulse.platform.criticalevents.domain.exceptions;

public class InvalidAlertException extends RuntimeException {

    public InvalidAlertException(String message) {
        super("Invalid alert: %s".formatted(message));
    }
}
