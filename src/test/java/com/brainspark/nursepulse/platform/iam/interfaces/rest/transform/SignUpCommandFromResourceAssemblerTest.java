package com.brainspark.nursepulse.platform.iam.interfaces.rest.transform;

import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.interfaces.rest.resources.SignUpResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignUpCommandFromResourceAssemblerTest {

    @Test
    void shouldMapSelectedDoctorRoleToSignUpCommand() {
        var resource = new SignUpResource(
                " Doctor.Maria ",
                "SecurePass123!",
                "ROLE_DOCTOR"
        );

        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);

        assertEquals("doctor.maria", command.username());
        assertEquals(Roles.ROLE_DOCTOR, command.roles().getFirst().getName());
    }
}
