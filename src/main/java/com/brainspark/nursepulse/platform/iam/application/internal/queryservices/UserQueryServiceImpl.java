package com.brainspark.nursepulse.platform.iam.application.internal.queryservices;

import com.brainspark.nursepulse.platform.iam.application.queryservices.UserQueryService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.brainspark.nursepulse.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.brainspark.nursepulse.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application service that resolves IAM user read queries.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> handle(GetAllUsersQuery query) {
        // Skip users with any role name no longer recognized by the current
        // Roles enum (e.g. left over from a role rename/merge without a data
        // migration) instead of letting the whole endpoint fail — see
        // RolesAttributeConverter.
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getStringName() == null))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByUsernameQuery query) {
        return userRepository.findByUsername(query.username());
    }
}
