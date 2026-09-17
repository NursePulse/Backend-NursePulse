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
 * @param firstName the user's first name
 * @param lastName the user's last name
 * @param phone the user's nine-digit Peruvian phone number
 * @param age the user's age
 * @param email the user's email address
 * @param roles the roles of the user
 *
 * @see com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User
 */
public record SignUpCommand(
        String username,
        String password,
        String firstName,
        String lastName,
        String phone,
        Integer age,
        String email,
        List<Role> roles
) {
    public SignUpCommand(String username, String password, List<Role> roles) {
        this(username, password, null, null, null, null, null, roles);
    }

    public SignUpCommand {
        username = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
        firstName = firstName == null ? null : firstName.trim();
        lastName = lastName == null ? null : lastName.trim();
        phone = phone == null ? null : phone.trim();
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
