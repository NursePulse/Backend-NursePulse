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
    String role,

    @NotBlank(message = "{validation.not-blank}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{1,20}$", message = "{validation.name.format}")
    @Schema(description = "First name", example = "Maria")
    String firstName,

    @NotBlank(message = "{validation.not-blank}")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{1,20}$", message = "{validation.name.format}")
    @Schema(description = "Last name", example = "Rodriguez")
    String lastName,

    @NotBlank(message = "{validation.not-blank}")
    @Email(message = "{validation.email}")
    @Size(max = 254, message = "{validation.size}")
    @Schema(description = "Contact email", example = "maria.rodriguez@example.com")
    String email,

    @NotBlank(message = "{validation.not-blank}")
    @Pattern(regexp = "^\\d{9}$", message = "{validation.phone.format}")
    @Schema(description = "Contact phone number (9 digits)", example = "987654321")
    String phone,

    @NotNull(message = "{validation.not-blank}")
    @Min(value = 18, message = "{validation.age.range}")
    @Max(value = 100, message = "{validation.age.range}")
    @Schema(description = "Age in years", example = "32")
    Integer age
) {
}
