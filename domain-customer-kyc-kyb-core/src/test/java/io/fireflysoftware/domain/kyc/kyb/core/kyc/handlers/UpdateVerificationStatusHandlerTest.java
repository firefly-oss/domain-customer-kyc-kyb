package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KycVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.VerifyKycCommand;
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
class UpdateVerificationStatusHandlerTest {

    @Mock
    private KycVerificationApi kycVerificationApi;

    private UpdateVerificationStatusHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateVerificationStatusHandler(kycVerificationApi);
    }

    @Test
    void doHandle_updatesStatusToVerified_completesEmpty() {
        VerifyKycCommand cmd = VerifyKycCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .verificationLevel("STANDARD")
                .build();

        KycVerificationDTO response = new KycVerificationDTO(null, null, cmd.getCaseId());
        when(kycVerificationApi.updateKycVerification(any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        VerifyKycCommand cmd = VerifyKycCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .verificationLevel("STANDARD")
                .build();

        when(kycVerificationApi.updateKycVerification(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("Update failed")
                .verify();
    }
}
