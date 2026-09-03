package com.brainspark.nursepulse.platform.iam.application.commandservices;

import com.brainspark.nursepulse.platform.iam.domain.model.commands.SeedRolesCommand;

/**
 * Application service contract for IAM role commands.
 */
public interface RoleCommandService {
    /**
     * Handles the role seeding command.
     *
     * @param command role-seeding command
     */
    void handle(SeedRolesCommand command);
}
