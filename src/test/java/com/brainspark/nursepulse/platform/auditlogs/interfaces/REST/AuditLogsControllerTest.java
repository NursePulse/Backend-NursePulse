package com.brainspark.nursepulse.platform.auditlogs.interfaces.REST;

import com.brainspark.nursepulse.platform.auditlogs.application.queryservices.AuditLogQueryService;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.commands.CreateAuditLogCommand;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditActionType;
import com.brainspark.nursepulse.platform.auditlogs.domain.model.valueobjects.AuditedEntityType;
import com.brainspark.nursepulse.platform.auditlogs.domain.services.AuditLogCommandService;
import com.brainspark.nursepulse.platform.auditlogs.interfaces.REST.resources.CreateAuditLogResource;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogsControllerTest {

    @Mock
    private AuditLogCommandService commandService;

    @Mock
    private AuditLogQueryService queryService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuditLogsController controller;

    @Test
    void shouldUseAuthenticatedUsernameInsteadOfClientSuppliedActor() {
        var resource = new CreateAuditLogResource(
                42L,
                AuditedEntityType.CLINICAL_EVENT,
                "event-7",
                AuditActionType.CREATE,
                "forged.user",
                Instant.parse("2026-07-06T12:00:00Z"),
                Map.of("description", "Clinical event created")
        );
        when(authentication.getName()).thenReturn("doctor.test");
        when(commandService.handle(any())).thenReturn(
                Result.failure(ApplicationError.unexpected("test", "stop after capture"))
        );

        controller.createAuditLog(resource, authentication);

        var commandCaptor = ArgumentCaptor.forClass(CreateAuditLogCommand.class);
        verify(commandService).handle(commandCaptor.capture());
        assertEquals("doctor.test", commandCaptor.getValue().performedBy());
    }
}
