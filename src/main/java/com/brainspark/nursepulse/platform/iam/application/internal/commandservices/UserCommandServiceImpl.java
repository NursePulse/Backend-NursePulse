package com.brainspark.nursepulse.platform.iam.application.internal.commandservices;

import com.brainspark.nursepulse.platform.iam.application.commandservices.UserCommandService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignInCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.UpdateUserRolesCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username());
        if (user.isEmpty()) {
            return invalidCredentials();
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return invalidCredentials();
        }
        var token = tokenService.generateToken(user.get().getUsername());
        return Result.success(ImmutablePair.of(user.get(), token));
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            return Result.failure(ApplicationError.conflict("User", "Username already exists"));
        }
        var requestedRoles = Role.validateRoleSet(command.roles());
        var roles = requestedRoles.stream()
                .map(role -> roleRepository.findByName(role.getName()))
                .toList();

        if (roles.stream().anyMatch(java.util.Optional::isEmpty)) {
            return Result.failure(ApplicationError.notFound("Role", "one or more role names"));
        }

        var resolvedRoles = roles.stream()
                .map(java.util.Optional::get)
                .toList();

        var user = new User(command.username(), hashingService.encode(command.password()), resolvedRoles);
        return Result.success(userRepository.save(user));
    }

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(UpdateUserRolesCommand command) {
        if (command.roles().isEmpty()) {
            return Result.failure(ApplicationError.validationError(
                    "roles",
                    "At least one role is required"
            ));
        }
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", String.valueOf(command.userId())));
        }
        if (user.get().getUsername().equals(command.requestedBy())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "update user roles",
                    "Administrators cannot change their own roles"
            ));
        }
        var resolvedRoles = new java.util.ArrayList<Role>();
        for (var requestedRole : command.roles()) {
            var persistedRole = roleRepository.findByName(requestedRole.getName());
            if (persistedRole.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Role", requestedRole.getStringName()));
            }
            resolvedRoles.add(persistedRole.get());
        }
        var targetUser = user.get();
        targetUser.setRoles(new java.util.HashSet<>(resolvedRoles));
        return Result.success(userRepository.save(targetUser));
    }

    private Result<ImmutablePair<User, String>, ApplicationError> invalidCredentials() {
        return Result.failure(ApplicationError.validationError(
                "credentials",
                "Invalid username or password"
        ));
    }
}
