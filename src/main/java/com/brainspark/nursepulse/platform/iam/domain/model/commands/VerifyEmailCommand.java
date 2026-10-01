package com.brainspark.nursepulse.platform.iam.domain.model.commands;

/**
 * Command to verify a user's email address using the token sent at sign-up.
 *
 * @param token the verification token
 */
public record VerifyEmailCommand(String token) {
}
