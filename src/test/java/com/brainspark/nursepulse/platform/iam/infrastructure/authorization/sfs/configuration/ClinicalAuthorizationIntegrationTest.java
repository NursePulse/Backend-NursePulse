package com.brainspark.nursepulse.platform.iam.infrastructure.authorization.sfs.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
class ClinicalAuthorizationIntegrationTest {

    private static final String RAILWAY_SWAGGER_ORIGIN =
            "https://backpulsereport-production-7576.up.railway.app";

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldAllowPublicAuthenticationEndpointWithoutToken() throws Exception {
        assertNotEquals(
                401,
                execute(HttpMethod.POST, "/api/v1/authentication/sign-in", null)
        );
    }

    @Test
    void shouldRejectClinicalEndpointWithoutToken() throws Exception {
        assertEquals(
                401,
                execute(HttpMethod.GET, "/api/v1/patients", null)
        );
    }

    @Test
    void shouldAllowSwaggerRequestsFromThePublicRailwayOrigin() throws Exception {
        var response = mockMvc.perform(
                        request(HttpMethod.OPTIONS, "/api/v1/authentication/sign-in")
                                .header(HttpHeaders.ORIGIN, RAILWAY_SWAGGER_ORIGIN)
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        HttpMethod.POST.name()
                                )
                )
                .andReturn()
                .getResponse();

        assertNotEquals(403, response.getStatus());
        assertEquals(
                RAILWAY_SWAGGER_ORIGIN,
                response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
        );
    }

    @ParameterizedTest
    @MethodSource("allowedClinicalRequests")
    void shouldAllowConfiguredClinicalRole(
            HttpMethod method,
            String path,
            String role
    ) throws Exception {
        assertNotEquals(403, execute(method, path, role));
    }

    @ParameterizedTest
    @MethodSource("forbiddenClinicalRequests")
    void shouldRejectRoleOutsideClinicalPermissionMatrix(
            HttpMethod method,
            String path,
            String role
    ) throws Exception {
        assertEquals(403, execute(method, path, role));
    }

    private int execute(HttpMethod method, String path, String role) throws Exception {
        var requestBuilder = request(method, path);
        if (role != null) {
            requestBuilder.with(user("clinical.user").authorities(() -> role));
        }

        return mockMvc.perform(requestBuilder)
                .andReturn()
                .getResponse()
                .getStatus();
    }

    private static Stream<Arguments> allowedClinicalRequests() {
        return Stream.of(
                Arguments.of(HttpMethod.POST, "/api/v1/patients", "ROLE_NURSE"),
                Arguments.of(HttpMethod.POST, "/api/v1/vital-sign-records", "ROLE_NURSE"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/handovers/7/acknowledge", "ROLE_NURSE"),
                Arguments.of(HttpMethod.GET, "/api/v1/patients", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/alerts/8/close", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.GET, "/api/v1/audit-logs", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.POST, "/api/v1/audit-logs", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.POST, "/api/v1/audit-logs", "ROLE_NURSE"),
                Arguments.of(HttpMethod.DELETE, "/api/v1/patients/9", "ROLE_ADMIN"),
                Arguments.of(HttpMethod.POST, "/api/v1/audit-logs", "ROLE_ADMIN")
        );
    }

    private static Stream<Arguments> forbiddenClinicalRequests() {
        return Stream.of(
                Arguments.of(HttpMethod.DELETE, "/api/v1/patients/9", "ROLE_NURSE"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/alerts/8/close", "ROLE_NURSE"),
                Arguments.of(HttpMethod.GET, "/api/v1/audit-logs", "ROLE_NURSE"),
                Arguments.of(HttpMethod.POST, "/api/v1/patients", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.POST, "/api/v1/vital-sign-records", "ROLE_DOCTOR"),
                Arguments.of(HttpMethod.PATCH, "/api/v1/handovers/7/acknowledge", "ROLE_DOCTOR")
        );
    }
}
