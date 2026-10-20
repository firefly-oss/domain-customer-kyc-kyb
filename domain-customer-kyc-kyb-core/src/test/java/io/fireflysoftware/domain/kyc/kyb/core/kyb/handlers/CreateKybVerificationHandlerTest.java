package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.OpenKybCaseCommand;
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
class CreateKybVerificationHandlerTest {

    @Mock
    private KybVerificationApi kybVerificationApi;

    private CreateKybVerificationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateKybVerificationHandler(kybVerificationApi);
    }

    @Test
    void doHandle_createsVerification_returnsId() {
        UUID expectedId = UUID.randomUUID();
        OpenKybCaseCommand cmd = OpenKybCaseCommand.builder()
                .partyId(UUID.randomUUID())
                .build();

        KybVerificationDTO response = new KybVerificationDTO(null, null, expectedId);
        when(kybVerificationApi.createKybVerification(any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(expectedId)
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        OpenKybCaseCommand cmd = OpenKybCaseCommand.builder()
                .partyId(UUID.randomUUID())
                .build();

        when(kybVerificationApi.createKybVerification(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API unavailable")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("API unavailable")
                .verify();
    }
}
