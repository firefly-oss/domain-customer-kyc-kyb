package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KycVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.FailKycCommand;
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
class FailKycHandlerTest {

    @Mock
    private KycVerificationApi kycVerificationApi;

    private FailKycHandler handler;

    @BeforeEach
    void setUp() {
        handler = new FailKycHandler(kycVerificationApi);
    }

    @Test
    void doHandle_failsVerification_completesEmpty() {
        FailKycCommand cmd = FailKycCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .reason("Fraudulent documents detected")
                .build();

        KycVerificationDTO response = new KycVerificationDTO(null, null, cmd.getCaseId());
        when(kycVerificationApi.updateKycVerification(any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        FailKycCommand cmd = FailKycCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .reason("Fraudulent documents detected")
                .build();

        when(kycVerificationApi.updateKycVerification(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("API error")
                .verify();
    }
}
