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
 * @param firstName the first name of the user
 * @param lastName the last name of the user
 * @param email the email of the user
 * @param phone the phone number of the user
 * @param age the age of the user
 * @param roles the roles of the user
 *
 * @see com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User
 */
public record SignUpCommand(
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        String phone,
        Integer age,
        List<Role> roles
) {
    public SignUpCommand {
        username = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    /**
     * Convenience constructor for internal/system-created users (e.g. bootstrap admin,
     * cross-context user creation) that do not collect a full clinical profile.
     */
    public SignUpCommand(String username, String password, List<Role> roles) {
        this(username, password, null, null, null, null, null, roles);
    }
}
