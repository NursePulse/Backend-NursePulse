package com.brainspark.nursepulse.platform.iam.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.iam.domain.model.commands.UpdateUserRolesCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.UpdateUserRolesResource;

/**
 * Assembler that converts {@link UpdateUserRolesResource} objects into {@link UpdateUserRolesCommand} commands.
 */
public class UpdateUserRolesCommandFromResourceAssembler {
    /**
     * Converts an update user roles resource to its command representation.
     *
     * @param userId identifier of the user whose roles are updated
     * @param resource update user roles resource
     * @param requestedBy username of the administrator performing the change
     * @return update user roles command
     */
    public static UpdateUserRolesCommand toCommandFromResource(
            Long userId,
            UpdateUserRolesResource resource,
            String requestedBy
    ) {
        var roles = resource.roles().stream().map(Role::toRoleFromName).toList();
        return new UpdateUserRolesCommand(userId, roles, requestedBy);
    }
}
