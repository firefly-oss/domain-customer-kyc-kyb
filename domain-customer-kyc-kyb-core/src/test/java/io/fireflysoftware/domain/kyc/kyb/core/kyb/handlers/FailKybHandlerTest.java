package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.FailKybCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailKybHandlerTest {

    @Mock
    private KybVerificationApi kybVerificationApi;

    private FailKybHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FailKybHandler(kybVerificationApi);
    }

    @Test
    void doHandle_failsVerification_completesEmpty() {
        FailKybCommand cmd = FailKybCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .reason("Opaque corporate structure")
                .build();

        KybVerificationDTO response = new KybVerificationDTO(null, null, cmd.getCaseId());
        when(kybVerificationApi.updateKybVerification(any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        FailKybCommand cmd = FailKybCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .reason("Sanctions hit")
                .build();

        when(kybVerificationApi.updateKybVerification(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("API error")
                .verify();
    }
}
