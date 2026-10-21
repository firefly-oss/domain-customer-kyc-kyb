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
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybResultQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKybResultHandlerTest {

    @Mock
    private KybVerificationApi kybVerificationApi;

    private GetKybResultHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetKybResultHandler(kybVerificationApi);
    }

    @Test
    void shouldReturnVerificationDtoFromApi() {
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        KybVerificationDTO expectedDto = new KybVerificationDTO(null, null, verificationId);
        expectedDto.verificationStatus("VERIFIED");
        when(kybVerificationApi.getKybVerification(eq(partyId), eq(verificationId), any()))
                .thenReturn(Mono.just(expectedDto));

        GetKybResultQuery query = GetKybResultQuery.builder()
                .partyId(partyId)
                .verificationId(verificationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .assertNext(dto -> {
                    assertThat(dto.getKybVerificationId()).isEqualTo(verificationId);
                    assertThat(dto.getVerificationStatus()).isEqualTo("VERIFIED");
                })
                .verifyComplete();

        verify(kybVerificationApi).getKybVerification(eq(partyId), eq(verificationId), any());
    }

    @Test
    void shouldPropagateErrorWhenVerificationNotFound() {
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        when(kybVerificationApi.getKybVerification(eq(partyId), eq(verificationId), any()))
                .thenReturn(Mono.error(new RuntimeException("Verification not found")));

        GetKybResultQuery query = GetKybResultQuery.builder()
                .partyId(partyId)
                .verificationId(verificationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectError(RuntimeException.class)
                .verify();
    }
}
