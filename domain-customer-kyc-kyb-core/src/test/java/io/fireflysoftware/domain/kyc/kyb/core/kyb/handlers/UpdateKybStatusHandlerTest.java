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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.VerifyKybCommand;
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
class UpdateKybStatusHandlerTest {

    @Mock
    private KybVerificationApi kybVerificationApi;

    private UpdateKybStatusHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateKybStatusHandler(kybVerificationApi);
    }

    @Test
    void doHandle_updatesStatusToVerified_completesEmpty() {
        VerifyKybCommand cmd = VerifyKybCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .build();

        KybVerificationDTO response = new KybVerificationDTO(null, null, cmd.getCaseId());
        when(kybVerificationApi.updateKybVerification(any(), any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(handler.doHandle(cmd))
                .verifyComplete();
    }

    @Test
    void doHandle_apiError_propagatesError() {
        VerifyKybCommand cmd = VerifyKybCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(UUID.randomUUID())
                .build();

        when(kybVerificationApi.updateKybVerification(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorMessage("Update failed")
                .verify();
    }
}
