package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Resource received to register a new IAM user.
 */
@Schema(
    name = "SignUpRequest",
    description = "Clinical staff sign-up request. Public registration only accepts nurse or doctor roles.",
    example = "{\"username\": \"doctor.maria\", \"password\": \"SecurePass123!\", \"role\": \"ROLE_DOCTOR\"}"
)
public record SignUpResource(
    @NotBlank(message = "{validation.not-blank}")
    @Size(min = 3, max = 50, message = "{validation.size}")
    @Schema(
        description = "Desired username",
        example = "nurse.maria",
        minLength = 3,
        maxLength = 50
    )
    String username,

    @NotBlank(message = "{validation.not-blank}")
    @Size(min = 8, max = 72, message = "{validation.size}")
    @Schema(
        description = "User password (8 to 72 characters)",
        example = "SecurePass123!",
        minLength = 8,
        maxLength = 72
    )
    String password,

    @NotBlank(message = "{validation.not-blank}")
    @Pattern(
        regexp = "ROLE_NURSE|ROLE_DOCTOR",
        message = "Public registration only accepts ROLE_NURSE or ROLE_DOCTOR"
    )
    @Schema(
        description = "Clinical role requested by the new user. ROLE_ADMIN can only be assigned by an administrator.",
        example = "ROLE_DOCTOR",
        allowableValues = {"ROLE_NURSE", "ROLE_DOCTOR"}
    )
    String role
) {
}
