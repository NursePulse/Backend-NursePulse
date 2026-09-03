package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Resource received to replace the roles assigned to a user.
 */
@Schema(
    name = "UpdateUserRolesRequest",
    description = "New set of roles for the user",
    example = "{\"roles\": [\"ROLE_DOCTOR\"]}"
)
public record UpdateUserRolesResource(
    @NotEmpty(message = "{validation.not-blank}")
    @ArraySchema(
        schema = @Schema(
            description = "Role name",
            example = "ROLE_DOCTOR",
            allowableValues = {"ROLE_NURSE", "ROLE_DOCTOR", "ROLE_ADMIN"}
        )
    )
    List<String> roles
) {
}
