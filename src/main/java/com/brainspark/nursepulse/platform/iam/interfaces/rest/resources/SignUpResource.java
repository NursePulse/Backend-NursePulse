package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Resource received to register a new IAM user.
 */
@Schema(
    name = "SignUpRequest",
    description = "Clinical staff sign-up request. Public registration only accepts nurse or doctor roles.",
    example = "{\"username\": \"doctor.maria\", \"password\": \"SecurePass123!\", \"firstName\": \"Maria\", \"lastName\": \"Lopez\", \"phone\": \"999999999\", \"age\": 30, \"email\": \"maria@example.com\", \"role\": \"ROLE_DOCTOR\"}"
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
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).+$",
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
    @Size(max = 50, message = "{validation.size}")
    @Pattern(regexp = "^[\\p{L}]+(?: [\\p{L}]+)*$", message = "{validation.name}")
    String firstName,

    @NotBlank(message = "{validation.not-blank}")
    @Size(max = 50, message = "{validation.size}")
    @Pattern(regexp = "^[\\p{L}]+(?: [\\p{L}]+)*$", message = "{validation.name}")
    String lastName,

    @NotBlank(message = "{validation.not-blank}")
    @Pattern(regexp = "^\\d{9}$", message = "{validation.phone}")
    String phone,

    @NotNull(message = "{validation.not-null}")
    @Min(value = 18, message = "{validation.min}")
    @Max(value = 120, message = "{validation.max}")
    Integer age,

    @NotBlank(message = "{validation.not-blank}")
    @Size(max = 254, message = "{validation.size}")
    @Email(message = "{validation.email}")
    String email,

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
