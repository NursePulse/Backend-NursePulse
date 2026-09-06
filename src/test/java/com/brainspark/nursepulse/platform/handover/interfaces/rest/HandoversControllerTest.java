package com.brainspark.nursepulse.platform.handover.interfaces.rest;

import com.brainspark.nursepulse.platform.handover.application.commandservices.HandoverCommandService;
import com.brainspark.nursepulse.platform.handover.application.queryservices.HandoverQueryService;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.AcknowledgeHandoverResource;
import com.brainspark.nursepulse.platform.handover.interfaces.rest.resources.CreateHandoverResource;
import com.brainspark.nursepulse.platform.iam.interfaces.acl.IamContextFacade;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies that the nurse identities on a handover (who registered it, who
 * acknowledged it) are always derived from the authenticated JWT, never from
 * client-supplied input - closing the hardcoded "incomingNurseId" gap found
 * during the EP-02/EP-03 review, where the acknowledging nurse used to be
 * whichever id the create-time "receiver" dropdown had picked, regardless of
 * who was actually logged in.
 */
@ExtendWith(MockitoExtension.class)
class HandoversControllerTest {

    @Mock
    private HandoverCommandService handoverCommandService;

    @Mock
    private HandoverQueryService handoverQueryService;

    @Mock
    private IamContextFacade iamContextFacade;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HandoversController controller;

    @Test
    void shouldUseAuthenticatedUsernameAsRegisteredByOnCreate() {
        var resource = new CreateHandoverResource(
                1L, "SBAR - Patient", "Stable", "Admitted 2 days ago", "Vitals normal", "Continue monitoring"
        );
        when(authentication.getName()).thenReturn("nurse.daniela");
        when(handoverCommandService.handle(any(CreateHandoverCommand.class))).thenReturn(
                Result.failure(ApplicationError.unexpected("test", "stop after capture"))
        );

        controller.createHandover(resource, authentication);

        var captor = ArgumentCaptor.forClass(CreateHandoverCommand.class);
        verify(handoverCommandService).handle(captor.capture());
        assertEquals("nurse.daniela", captor.getValue().registeredBy());
    }

    @Test
    void shouldDeriveIncomingNurseIdFromAuthenticatedUserOnAcknowledge() {
        var resource = new AcknowledgeHandoverResource("Taking over shift");
        when(authentication.getName()).thenReturn("nurse.laura");
        when(iamContextFacade.fetchUserIdByUsername("nurse.laura")).thenReturn(7L);
        when(handoverCommandService.handle(any(AcknowledgeHandoverCommand.class))).thenReturn(
                Result.failure(ApplicationError.unexpected("test", "stop after capture"))
        );

        controller.acknowledgeHandover(99L, resource, authentication);

        var captor = ArgumentCaptor.forClass(AcknowledgeHandoverCommand.class);
        verify(handoverCommandService).handle(captor.capture());
        assertEquals(7L, captor.getValue().incomingNurseId());
        assertEquals(99L, captor.getValue().handoverId());
    }
}
