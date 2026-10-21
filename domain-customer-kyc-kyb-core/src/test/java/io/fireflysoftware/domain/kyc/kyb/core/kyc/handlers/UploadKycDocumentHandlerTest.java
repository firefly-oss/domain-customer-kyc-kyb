/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
