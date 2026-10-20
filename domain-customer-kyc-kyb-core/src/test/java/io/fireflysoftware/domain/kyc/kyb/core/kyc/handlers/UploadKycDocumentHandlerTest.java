package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationDocumentsApi;
import io.fireflysoftware.core.kycb.sdk.model.VerificationDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachKycEvidenceCommand;
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
class UploadKycDocumentHandlerTest {

    @Mock
    private KycVerificationDocumentsApi kycVerificationDocumentsApi;

    private UploadKycDocumentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UploadKycDocumentHandler(kycVerificationDocumentsApi);
    }

    @Test
    void doHandle_uploadsDocument_returnsDocumentId() {
        UUID expectedDocId = UUID.randomUUID();
        AttachKycEvidenceCommand cmd = AttachKycEvidenceCommand.builder()
                .partyId(UUID.randomUUID())
                .caseId(UUID.randomUUID())
                .documentType("PASSPORT")
                .fingerprint("abc123hash")
                .build();

        VerificationDocumentDTO response = new VerificationDocumentDTO(null, null, expectedDocId);
        when(kycVerificationDocumentsApi.addVerificationDocument(any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(expectedDocId)
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        AttachKycEvidenceCommand cmd = AttachKycEvidenceCommand.builder()
                .partyId(UUID.randomUUID())
                .caseId(UUID.randomUUID())
                .documentType("PASSPORT")
                .fingerprint("abc123hash")
                .build();

        when(kycVerificationDocumentsApi.addVerificationDocument(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Upload failed")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("Upload failed")
                .verify();
    }
}
