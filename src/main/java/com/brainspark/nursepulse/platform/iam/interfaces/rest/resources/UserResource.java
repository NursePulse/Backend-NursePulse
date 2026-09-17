package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Resource representing an IAM user returned by the REST API.
 */
@Schema(
    name = "UserResponse",
    description = "User information response",
    example = "{\"id\": 1, \"username\": \"nurse.maria\", \"firstName\": \"Maria\", \"lastName\": \"Lopez\", \"phone\": \"999999999\", \"age\": 30, \"email\": \"maria@example.com\", \"roles\": [\"ROLE_NURSE\"]}"
)
public record UserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User username", example = "nurse.maria")
    String username,

    String firstName,

    String lastName,

    String phone,

    Integer age,

    String email,

    @Schema(description = "User assigned roles", example = "[\"ROLE_NURSE\"]")
    List<String> roles
) {
}
