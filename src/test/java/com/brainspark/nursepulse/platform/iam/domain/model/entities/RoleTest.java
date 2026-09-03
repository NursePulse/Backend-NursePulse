package com.brainspark.nursepulse.platform.iam.domain.model.entities;

import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleTest {

    @Test
    void shouldUseNurseAsDefaultRole() {
        assertEquals(Roles.ROLE_NURSE, Role.getDefaultRole().getName());
        assertEquals(
                List.of(Role.getDefaultRole()),
                Role.validateRoleSet(List.of())
        );
    }

    @Test
    void shouldParseRoleNamesCaseInsensitively() {
        assertEquals(
                Roles.ROLE_DOCTOR,
                Role.toRoleFromName("role_doctor").getName()
        );
    }

    @Test
    void shouldRejectBlankRoleNames() {
        assertThrows(IllegalArgumentException.class, () -> Role.toRoleFromName(" "));
    }
}
