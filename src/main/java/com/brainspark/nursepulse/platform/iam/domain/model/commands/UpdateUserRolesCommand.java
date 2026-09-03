package com.brainspark.nursepulse.platform.iam.domain.model.commands;

import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;

import java.util.List;
import java.util.Locale;

/**
 * Update user roles command
 * <p>
 *     This class represents the command to replace the roles assigned to a user.
 * </p>
 * @param userId the identifier of the user whose roles are updated
 * @param roles the new set of roles for the user
 * @param requestedBy the username of the administrator performing the change
 *
 * @see com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User
 */
public record UpdateUserRolesCommand(Long userId, List<Role> roles, String requestedBy) {
    public UpdateUserRolesCommand {
        roles = roles == null ? List.of() : List.copyOf(roles);
        requestedBy = requestedBy == null ? null : requestedBy.trim().toLowerCase(Locale.ROOT);
    }
}
