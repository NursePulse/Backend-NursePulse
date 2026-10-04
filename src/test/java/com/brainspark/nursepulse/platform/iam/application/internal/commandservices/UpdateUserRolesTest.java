package com.brainspark.nursepulse.platform.iam.application.internal.commandservices;

import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.brainspark.nursepulse.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.brainspark.nursepulse.platform.iam.domain.model.aggregates.User;
import com.brainspark.nursepulse.platform.iam.domain.model.commands.UpdateUserRolesCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** US-26: an administrator manages users and assigns roles. */
class UpdateUserRolesTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        service = new UserCommandServiceImpl(userRepository, mock(HashingService.class), mock(TokenService.class),
                roleRepository, mock(EmailNotificationService.class), "https://example.test");
    }

    private User nurse(String username) {
        return new User(username, "hash", "Ana", "Pérez", username + "@example.com", "912345678", 30,
                List.of(new Role(Roles.ROLE_NURSE)));
    }

    @Test
    void assignsTheRequestedRoleToAnotherUser() {
        var target = nurse("nurse.ana");
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(roleRepository.findByName(Roles.ROLE_DOCTOR)).thenReturn(Optional.of(new Role(Roles.ROLE_DOCTOR)));
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new UpdateUserRolesCommand(2L, List.of(new Role(Roles.ROLE_DOCTOR)), "admin.root"));

        var updated = (User) assertInstanceOf(Result.Success.class, result).value();
        assertEquals(1, updated.getRoles().size());
        assertEquals(Roles.ROLE_DOCTOR, updated.getRoles().iterator().next().getName());
    }

    @Test
    void rejectsAnEmptyRoleList() {
        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                service.handle(new UpdateUserRolesCommand(2L, List.of(), "admin.root"))).error();

        assertEquals("VALIDATION_ERROR", error.code());
        verify(userRepository, never()).save(any());
    }

    @Test
    void returnsNotFoundForUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                service.handle(new UpdateUserRolesCommand(99L, List.of(new Role(Roles.ROLE_DOCTOR)), "admin.root"))).error();

        assertEquals("USER_NOT_FOUND", error.code());
    }

    @Test
    void anAdministratorCannotChangeTheirOwnRoles() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(nurse("admin.root")));

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                service.handle(new UpdateUserRolesCommand(1L, List.of(new Role(Roles.ROLE_NURSE)), "Admin.Root "))).error();

        assertEquals("BUSINESS_RULE_VIOLATION", error.code());
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectsARoleThatIsNotPersisted() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(nurse("nurse.ana")));
        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());

        var error = (ApplicationError) assertInstanceOf(Result.Failure.class,
                service.handle(new UpdateUserRolesCommand(2L, List.of(new Role(Roles.ROLE_ADMIN)), "admin.root"))).error();

        assertEquals("ROLE_NOT_FOUND", error.code());
    }
}
