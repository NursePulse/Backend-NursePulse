package com.brainspark.nursepulse.platform.iam.interfaces.rest.resources;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignUpResourceValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static SignUpResource resourceWithRole(String username, String password, String role) {
        return new SignUpResource(
                username,
                password,
                role,
                "Maria",
                "Rodriguez",
                "clinical.user@example.com",
                "987654321",
                32
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_NURSE", "ROLE_DOCTOR"})
    void shouldAcceptPublicClinicalRoles(String role) {
        var resource = resourceWithRole("clinical.user", "SecurePass123!", role);

        assertTrue(validator.validate(resource).isEmpty());
    }

    @Test
    void shouldRejectAdminRoleDuringPublicRegistration() {
        var resource = resourceWithRole("admin.user", "SecurePass123!", "ROLE_ADMIN");

        assertFalse(validator.validate(resource).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SecurePass123!", "Another$Valid1", "Twelve+Chars"})
    void shouldAcceptPasswordsMeetingTheComplexityPolicy(String password) {
        var resource = resourceWithRole("clinical.user", password, "ROLE_NURSE");

        assertTrue(validator.validate(resource).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Short1!",           // fewer than 12 characters
            "ThisPasswordIsWayTooLong1!", // more than 20 characters
            "lowercase123!",     // no uppercase letter
            "NoSpecialChar123",  // no special character
    })
    void shouldRejectPasswordsViolatingTheComplexityPolicy(String password) {
        var resource = resourceWithRole("clinical.user", password, "ROLE_NURSE");

        assertFalse(validator.validate(resource).isEmpty());
    }
}
