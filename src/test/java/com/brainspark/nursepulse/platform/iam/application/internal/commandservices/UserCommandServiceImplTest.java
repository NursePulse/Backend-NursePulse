package com.brainspark.nursepulse.platform.iam.application.internal.commandservices;

import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignInCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.SignUpCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.brainspark.nursepulse.platform.iam.domain.model.entities.Role;
import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.domain.repositories.RoleRepository;
import com.brainspark.nursepulse.platform.iam.domain.repositories.UserRepository;
import com.brainspark.nursepulse.platform.shared.application.notifications.EmailNotificationService;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
                emailNotificationService,
                "https://backend-nursepulse-qfct.onrender.com"
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
        // No email was provided (internal/system user), so it stays verified by default.
        assertTrue(createdUser.isEmailVerified());
        verify(emailNotificationService, never()).sendVerificationEmail(any(), any(), any());
    }

    @Test
    void shouldSendVerificationEmailAndMarkAccountUnverifiedAfterSignUp() {
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
        assertFalse(createdUser.isEmailVerified());
        verify(emailNotificationService).sendVerificationEmail(
                eq("maria@example.com"),
                eq("Maria"),
                anyString()
        );
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
        verify(emailNotificationService, never()).sendVerificationEmail(any(), any(), any());
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
        verify(emailNotificationService, never()).sendVerificationEmail(any(), any(), any());
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

    @Test
    void shouldSignInSuccessfullyWhenEmailIsVerified() {
        var user = new User("nurse.maria", "encoded-password");
        user.setEmailVerified(true);
        when(userRepository.findByUsername("nurse.maria")).thenReturn(Optional.of(user));
        when(hashingService.matches("SecurePass123!", "encoded-password")).thenReturn(true);
        when(tokenService.generateToken("nurse.maria")).thenReturn("jwt-token");

        var result = service.handle(new SignInCommand("nurse.maria", "SecurePass123!"));

        var success = assertInstanceOf(Result.Success.class, result);
        var pair = assertInstanceOf(org.apache.commons.lang3.tuple.ImmutablePair.class, success.value());
        assertEquals("jwt-token", pair.getRight());
    }

    @Test
    void shouldRejectSignInWhenEmailIsNotVerified() {
        var user = new User("nurse.maria", "encoded-password");
        user.setEmailVerified(false);
        when(userRepository.findByUsername("nurse.maria")).thenReturn(Optional.of(user));
        when(hashingService.matches("SecurePass123!", "encoded-password")).thenReturn(true);

        var result = service.handle(new SignInCommand("nurse.maria", "SecurePass123!"));

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void shouldVerifyEmailWithValidToken() {
        var user = new User("nurse.maria", "encoded-password");
        user.setEmailVerified(false);
        user.setVerificationToken("valid-token");
        user.setVerificationTokenExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(new VerifyEmailCommand("valid-token"));

        var success = assertInstanceOf(Result.Success.class, result);
        var verifiedUser = assertInstanceOf(User.class, success.value());
        assertTrue(verifiedUser.isEmailVerified());
        assertEquals(null, verifiedUser.getVerificationToken());
    }

    @Test
    void shouldRejectVerificationWithUnknownToken() {
        when(userRepository.findByVerificationToken("unknown-token")).thenReturn(Optional.empty());

        var result = service.handle(new VerifyEmailCommand("unknown-token"));

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("VERIFICATIONTOKEN_NOT_FOUND", error.code());
    }

    @Test
    void shouldRejectVerificationWithExpiredToken() {
        var user = new User("nurse.maria", "encoded-password");
        user.setEmailVerified(false);
        user.setVerificationToken("expired-token");
        user.setVerificationTokenExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS));
        when(userRepository.findByVerificationToken("expired-token")).thenReturn(Optional.of(user));

        var result = service.handle(new VerifyEmailCommand("expired-token"));

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        verify(userRepository, never()).save(any());
    }
}
