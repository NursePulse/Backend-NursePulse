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
                Roles.ROLE_HEAD_NURSE,
                Role.toRoleFromName("role_head_nurse").getName()
        );
    }

    @Test
    void shouldRejectBlankRoleNames() {
        assertThrows(IllegalArgumentException.class, () -> Role.toRoleFromName(" "));
    }
}
