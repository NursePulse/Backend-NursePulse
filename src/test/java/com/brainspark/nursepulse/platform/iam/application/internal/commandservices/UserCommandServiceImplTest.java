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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserCommandServiceImplTest {
    private UserRepository userRepository;
    private HashingService hashingService;
    private TokenService tokenService;
    private RoleRepository roleRepository;
    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        hashingService = mock(HashingService.class);
        tokenService = mock(TokenService.class);
        roleRepository = mock(RoleRepository.class);
        service = new UserCommandServiceImpl(
                userRepository,
                hashingService,
                tokenService,
                roleRepository
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
    void shouldReturnGenericCredentialsErrorWhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        var result = service.handle(new SignInCommand("unknown", "SecurePass123!"));

        var failure = assertInstanceOf(Result.Failure.class, result);
        var error = assertInstanceOf(ApplicationError.class, failure.error());
        assertEquals("VALIDATION_ERROR", error.code());
        assertEquals("Invalid username or password", error.details());
    }
}
