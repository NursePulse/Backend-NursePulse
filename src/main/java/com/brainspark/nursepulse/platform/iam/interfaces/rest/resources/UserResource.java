package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Resource representing an IAM user returned by the REST API.
 */
@Schema(
    name = "UserResponse",
    description = "User information response",
    example = "{\"id\": 1, \"username\": \"nurse.maria\", \"firstName\": \"Maria\", \"lastName\": \"Rodriguez\", \"email\": \"maria.rodriguez@example.com\", \"phone\": \"987654321\", \"age\": 32, \"roles\": [\"ROLE_NURSE\"]}"
)
public record UserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User username", example = "nurse.maria")
    String username,

    @Schema(description = "First name", example = "Maria")
    String firstName,

    @Schema(description = "Last name", example = "Rodriguez")
    String lastName,

    @Schema(description = "Contact email", example = "maria.rodriguez@example.com")
    String email,

    @Schema(description = "Contact phone number", example = "987654321")
    String phone,

    @Schema(description = "Age in years", example = "32")
    Integer age,

    @Schema(description = "User assigned roles", example = "[\"ROLE_NURSE\"]")
    List<String> roles
) {
}
