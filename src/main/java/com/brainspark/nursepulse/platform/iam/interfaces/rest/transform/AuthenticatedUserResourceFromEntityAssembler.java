package com.brainspark.nursepulse.platform.iam.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Assembler that translates IAM authentication results into {@link AuthenticatedUserResource}.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    /**
     * Creates a resource from the authenticated {@link User} aggregate and issued bearer token.
     *
     * @param user authenticated user aggregate
     * @param token generated bearer token
     * @return resource used by the authentication endpoint response
     */
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        var roles = user.getRoles().stream()
                .map(com.brainspark.nursepulse.platform.iam.domain.model.entities.Role::getStringName)
                .sorted()
                .toList();
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), roles, token);
    }
}
