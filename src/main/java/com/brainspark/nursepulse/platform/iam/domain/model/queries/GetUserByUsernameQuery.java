package com.brainspark.nursepulse.platform.iam.domain.model.queries;

import java.util.Locale;

/**
 * Get user by username query
 * <p>
 *     This class represents the query to get a user by its username.
 * </p>
 * @param username the username of the user
 */
public record GetUserByUsernameQuery(String username) {
    public GetUserByUsernameQuery {
        username = username == null ? null : username.trim().toLowerCase(Locale.ROOT);
    }
}
