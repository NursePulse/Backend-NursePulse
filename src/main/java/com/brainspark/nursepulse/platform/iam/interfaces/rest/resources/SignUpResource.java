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
    @Size(min = 12, max = 20, message = "{validation.size}")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9\\s]).+$",
        message = "{validation.password.complexity}"
    )
    @Schema(
        description = "User password (12 to 20 characters, including at least one uppercase letter and one special character)",
        example = "SecurePass123!",
        minLength = 12,
        maxLength = 20
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
