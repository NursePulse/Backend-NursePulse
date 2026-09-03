package com.brainspark.nursepulse.platform.iam.domain.model.commands;

import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;

import java.util.List;
import java.util.Locale;

/**
 * Sign up command
 * <p>
 *     This class represents the command to sign up a user.
 * </p>
 * @param username the username of the user
 * @param password the password of the user
 * @param roles the roles of the user
 *
 * @see com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User
 */
public record SignUpCommand(String username, String password, List<Role> roles) {
    public SignUpCommand {
        username = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
