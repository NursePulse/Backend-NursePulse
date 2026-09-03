package com.brainspark.nursepulse.platform.iam.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.SignUpResource;

import java.util.List;

/**
 * Assembler that translates {@link SignUpResource} into {@link SignUpCommand}.
 */
public class SignUpCommandFromResourceAssembler {
    /**
     * Converts the incoming sign-up resource to an application command.
     *
     * @param resource sign-up payload from REST API
     * @return sign-up command consumed by the application layer
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                resource.username(),
                resource.password(),
                List.of(Role.toRoleFromName(resource.role()))
        );
    }
}
