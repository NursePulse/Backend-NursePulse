package com.brainspark.nursepulse.platform.iam.application.internal.commandservices;

import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignInCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.notifications.EmailNotificationService;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCommandServiceImplTest {
    private UserRepository userRepository;
    private HashingService hashingService;
    private TokenService tokenService;
    private RoleRepository roleRepository;
    private EmailNotificationService emailNotificationService;
    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        hashingService = mock(HashingService.class);
        tokenService = mock(TokenService.class);
        roleRepository = mock(RoleRepository.class);
        emailNotificationService = mock(EmailNotificationService.class);
        service = new UserCommandServiceImpl(
                userRepository,
                hashingService,
                tokenService,
                roleRepository,
                emailNotificationService
        );
    }

    @Test
    void shouldCreateUserWithEncodedPasswordAndResolvedRole() {
        var persistedRole = new Role(1L, Roles.ROLE_NURSE);
        var command = new SignUpCommand(
                " Nurse.Maria ",
                "SecurePass123!",
                List.of(Role.getDefaultRole())
        );
        when(userRepository.existsByUsername("nurse.maria")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_NURSE)).thenReturn(Optional.of(persistedRole));
        when(hashingService.encode("SecurePass123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        var result = service.handle(command);

        var success = assertInstanceOf(Result.Success.class, result);
        var createdUser = assertInstanceOf(User.class, success.value());
        assertEquals(10L, createdUser.getId());
        assertEquals("nurse.maria", createdUser.getUsername());
        assertEquals("encoded-password", createdUser.getPassword());
        assertEquals(Roles.ROLE_NURSE, createdUser.getRoles().iterator().next().getName());
        verify(hashingService).encode("SecurePass123!");
    }

    @Test
    void shouldSendWelcomeEmailAfterSuccessfulSignUp() {
        var persistedRole = new Role(1L, Roles.ROLE_NURSE);
        var command = new SignUpCommand(
                "nurse.maria",
                "SecurePass123!",
                "Maria",
                "Rodriguez",
                "maria@example.com",
                "987654321",
                30,
                List.of(Role.getDefaultRole())
        );
        when(userRepository.existsByUsername("nurse.maria")).thenReturn(false);
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("987654321")).thenReturn(false);
        when(roleRepository.findByName(Roles.ROLE_NURSE)).thenReturn(Optional.of(persistedRole));
        when(hashingService.encode("SecurePass123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(11L);
            return user;
        });

        var result = service.handle(command);

        var success = assertInstanceOf(Result.Success.class, result);
        var createdUser = assertInstanceOf(User.class, success.value());
        assertEquals("maria@example.com", createdUser.getEmail());
        verify(emailNotificationService).sendWelcomeEmail("maria@example.com", "Maria");
    }

    @Test
    void shouldRejectSignUpWhenEmailAlreadyExists() {
        var command = new SignUpCommand(
                "nurse.maria",
                "SecurePass123!",
                "Maria",
                "Rodriguez",
                "maria@example.com",
                "987654321",
                30,
                List.of(Role.getDefaultRole())
        );
        when(userRepository.existsByUsername("nurse.maria")).thenReturn(false);
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(true);

        var result = service.handle(command);

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("USER_CONFLICT", error.code());
        verify(hashingService, never()).encode(any());
        verify(emailNotificationService, never()).sendWelcomeEmail(any(), any());
    }

    @Test
    void shouldRejectSignUpWhenPhoneAlreadyExists() {
        var command = new SignUpCommand(
                "nurse.maria",
                "SecurePass123!",
                "Maria",
                "Rodriguez",
                "maria@example.com",
                "987654321",
                30,
                List.of(Role.getDefaultRole())
        );
        when(userRepository.existsByUsername("nurse.maria")).thenReturn(false);
        when(userRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("987654321")).thenReturn(true);

        var result = service.handle(command);

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("USER_CONFLICT", error.code());
        verify(hashingService, never()).encode(any());
        verify(emailNotificationService, never()).sendWelcomeEmail(any(), any());
    }

    @Test
    void shouldReturnGenericCredentialsErrorWhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        var result = service.handle(new SignInCommand("unknown", "SecurePass123!"));

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals("Invalid username or password", error.details());
    }
}
