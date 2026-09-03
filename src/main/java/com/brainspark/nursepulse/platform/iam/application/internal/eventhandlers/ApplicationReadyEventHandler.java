package com.brainspark.nursepulse.platform.iam.application.internal.eventhandlers;

import com.brainspark.nursepulse.platform.iam.application.commandservices.RoleCommandService;
import com.brainspark.nursepulse.platform.iam.application.commandservices.UserCommandService;
import com.brainspark.nursepulse.platform.iam.application.queryservices.UserQueryService;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SeedRolesCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Application lifecycle handler that ensures IAM roles are seeded when the application is ready.
 */
@Service
@Slf4j
public class ApplicationReadyEventHandler {
    private final RoleCommandService roleCommandService;
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final String bootstrapAdminUsername;
    private final String bootstrapAdminPassword;

    public ApplicationReadyEventHandler(
            RoleCommandService roleCommandService,
            UserCommandService userCommandService,
            UserQueryService userQueryService,
            @Value("${iam.bootstrap.admin.username:}") String bootstrapAdminUsername,
            @Value("${iam.bootstrap.admin.password:}") String bootstrapAdminPassword
    ) {
        this.roleCommandService = roleCommandService;
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.bootstrapAdminUsername = bootstrapAdminUsername;
        this.bootstrapAdminPassword = bootstrapAdminPassword;
    }

    /**
     * Handles the Spring application-ready event and triggers role seeding verification.
     *
     * @param event Spring Boot readiness event
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        log.info("Starting to verify if roles seeding is needed for {} at {}", applicationName, currentTimestamp());
        var seedRolesCommand = new SeedRolesCommand();
        roleCommandService.handle(seedRolesCommand);
        seedBootstrapAdmin();
        log.info("Roles seeding verification finished for {} at {}", applicationName, currentTimestamp());
    }

    private void seedBootstrapAdmin() {
        if (bootstrapAdminUsername.isBlank() && bootstrapAdminPassword.isBlank()) {
            return;
        }
        if (bootstrapAdminUsername.isBlank() || bootstrapAdminPassword.isBlank()) {
            log.warn("Bootstrap administrator was not created because both IAM admin properties are required");
            return;
        }
        if (bootstrapAdminPassword.length() < 12) {
            log.warn("Bootstrap administrator was not created because its password must contain at least 12 characters");
            return;
        }
        if (userQueryService.handle(new GetUserByUsernameQuery(bootstrapAdminUsername)).isPresent()) {
            return;
        }

        var command = new SignUpCommand(
                bootstrapAdminUsername,
                bootstrapAdminPassword,
                List.of(new Role(Roles.ROLE_ADMIN))
        );
        var result = userCommandService.handle(command);
        if (result instanceof Result.Success<?, ?>) {
            log.info("Bootstrap administrator account created");
        } else {
            log.warn("Bootstrap administrator account could not be created");
        }
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
