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

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_NURSE", "ROLE_DOCTOR"})
    void shouldAcceptPublicClinicalRoles(String role) {
        var resource = validResource("SecurePass123!", role);

        assertTrue(validator.validate(resource).isEmpty());
    }

    @Test
    void shouldRejectAdminRoleDuringPublicRegistration() {
        var resource = validResource("SecurePass123!", "ROLE_ADMIN");

        assertFalse(validator.validate(resource).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SecurePass123!", "Another$Valid1", "Twelve1+Chars"})
    void shouldAcceptPasswordsMeetingTheComplexityPolicy(String password) {
        var resource = validResource(password, "ROLE_NURSE");

        assertTrue(validator.validate(resource).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Short1!",           // fewer than 12 characters
            "ThisPasswordIsWayTooLong1!", // more than 20 characters
            "lowercase123!",     // no uppercase letter
            "NoSpecialChar123",  // no special character
            "Passwordabcdef!",   // no number
    })
    void shouldRejectPasswordsViolatingTheComplexityPolicy(String password) {
        var resource = validResource(password, "ROLE_NURSE");

        assertFalse(validator.validate(resource).isEmpty());
    }

    @Test
    void shouldAcceptSpanishCharactersInNames() {
        var resource = new SignUpResource(
                "clinical.user",
                "SecurePass123!",
                "Ángel",
                "Muñoz",
                "999999999",
                30,
                "angel@example.com",
                "ROLE_NURSE"
        );

        assertTrue(validator.validate(resource).isEmpty());
    }

    @Test
    void shouldRejectInvalidProfileFields() {
        var resource = new SignUpResource(
                "clinical.user",
                "SecurePass123!",
                "Maria2",
                "Lopez",
                "99999999",
                17,
                "not-an-email",
                "ROLE_NURSE"
        );

        assertFalse(validator.validate(resource).isEmpty());
    }

    private static SignUpResource validResource(String password, String role) {
        return new SignUpResource(
                "clinical.user",
                password,
                "Maria",
                "Lopez",
                "999999999",
                30,
                "maria@example.com",
                role
        );
    }
}
