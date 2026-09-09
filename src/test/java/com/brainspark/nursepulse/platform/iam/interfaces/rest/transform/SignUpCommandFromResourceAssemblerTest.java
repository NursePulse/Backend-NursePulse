package com.brainspark.nursepulse.platform.iam.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.SignUpResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignUpCommandFromResourceAssemblerTest {

    @Test
    void shouldMapSelectedHeadNurseRoleToSignUpCommand() {
        var resource = new SignUpResource(
                " HeadNurse.Maria ",
                "SecurePass123!",
                "ROLE_HEAD_NURSE"
        );

        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);

        assertEquals("headnurse.maria", command.username());
        assertEquals(Roles.ROLE_HEAD_NURSE, command.roles().getFirst().getName());
    }
}
