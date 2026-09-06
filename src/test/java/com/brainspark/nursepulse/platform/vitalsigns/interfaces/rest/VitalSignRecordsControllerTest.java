package com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest;

import com.brainspark.nursepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import com.brainspark.nursepulse.platform.vitalsigns.application.commandservices.VitalSignRecordCommandService;
import com.brainspark.nursepulse.platform.vitalsigns.application.queryservices.VitalSignRecordQueryService;
import com.brainspark.nursepulse.platform.vitalsigns.domain.model.commands.CreateVitalSignRecordCommand;
import com.brainspark.nursepulse.platform.vitalsigns.interfaces.rest.resources.CreateVitalSignRecordResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * Verifies that the nurse responsible for a vital sign record is always derived
 * from the authenticated JWT (via {@link IamContextFacade}), never trusted from
 * client-supplied input - closing the hardcoded "nurseId" gap found during the
 * EP-02/EP-03 review.
 */
@ExtendWith(MockitoExtension.class)
class VitalSignRecordsControllerTest {

    @Mock
    private VitalSignRecordCommandService vitalSignRecordCommandService;

    @Mock
    private VitalSignRecordQueryService vitalSignRecordQueryService;

    @Mock
    private IamContextFacade iamContextFacade;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VitalSignRecordsController controller;

    @Test
    void shouldDeriveNurseIdFromAuthenticatedUserInsteadOfClientInput() {
        var resource = new CreateVitalSignRecordResource(
                1L, 80, 16, 120, 80, 97, new BigDecimal("36.5"), null
        );
        when(authentication.getName()).thenReturn("nurse.daniela");
        when(iamContextFacade.fetchUserIdByUsername("nurse.daniela")).thenReturn(42L);
        when(vitalSignRecordCommandService.handle(any())).thenReturn(
                Result.failure(ApplicationError.unexpected("test", "stop after capture"))
        );

        controller.createVitalSignRecord(resource, authentication);

        var commandCaptor = ArgumentCaptor.forClass(CreateVitalSignRecordCommand.class);
        verify(vitalSignRecordCommandService).handle(commandCaptor.capture());
        assertEquals(42L, commandCaptor.getValue().nurseId());
    }
}
