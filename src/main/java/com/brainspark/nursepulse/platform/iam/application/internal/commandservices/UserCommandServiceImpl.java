package com.brainspark.nursepulse.platform.iam.application.internal.commandservices;

import com.brainspark.nursepulse.platform.iam.application.commandservices.UserCommandService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignInCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.UpdateUserRolesCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.notifications.EmailNotificationService;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final EmailNotificationService emailNotificationService;
    private final String publicUrl;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            RoleRepository roleRepository,
            EmailNotificationService emailNotificationService,
            @Value("${app.public-url:http://localhost:8080}") String publicUrl) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.emailNotificationService = emailNotificationService;
        this.publicUrl = publicUrl;
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
        if (!user.get().isEmailVerified()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "sign in",
                    "Email not verified. Check your inbox for the confirmation link."
            ));
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
        if (command.email() != null && userRepository.existsByEmail(command.email())) {
            return Result.failure(ApplicationError.conflict("User", "Email already exists"));
        }
        if (command.phone() != null && userRepository.existsByPhone(command.phone())) {
            return Result.failure(ApplicationError.conflict("User", "Phone already exists"));
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

        var user = new User(
                command.username(),
                hashingService.encode(command.password()),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.phone(),
                command.age(),
                resolvedRoles
        );

        if (command.email() != null && !command.email().isBlank()) {
            user.setEmailVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setVerificationTokenExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        }

        var savedUser = userRepository.save(user);

        if (!savedUser.isEmailVerified()) {
            var verificationLink = publicUrl + "/api/v1/authentication/verify-email?token=" + savedUser.getVerificationToken();
            emailNotificationService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), verificationLink);
        }

        return Result.success(savedUser);
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

    @Override
    @Transactional
    public Result<User, ApplicationError> handle(VerifyEmailCommand command) {
        var userOptional = userRepository.findByVerificationToken(command.token());
        if (userOptional.isEmpty()) {
            return Result.failure(ApplicationError.notFound("VerificationToken", command.token()));
        }

        var user = userOptional.get();
        if (user.getVerificationTokenExpiresAt() == null || user.getVerificationTokenExpiresAt().isBefore(Instant.now())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "verify email",
                    "Verification link has expired"
            ));
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        return Result.success(userRepository.save(user));
    }

    private Result<ImmutablePair<User, String>, ApplicationError> invalidCredentials() {
        return Result.failure(ApplicationError.validationError(
                "credentials",
                "Invalid username or password"
        ));
    }
}
