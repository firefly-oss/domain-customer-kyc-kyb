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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.adapters;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.KybProviderPort;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stub implementation of {@link KybProviderPort} for development and testing.
 *
 * <p>Returns deterministic, realistic results simulating a successful KYB verification.
 * Active by default when no real provider is configured.
 *
 * @see KybProviderPort
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "kyb.provider", havingValue = "stub", matchIfMissing = true)
public class StubKybProviderAdapter implements KybProviderPort {

    private static final String PROVIDER_NAME = "STUB";
    private static final int STUB_BUSINESS_SCORE = 85;
    private static final int STUB_DOCUMENT_CONFIDENCE = 92;
    private static final int STUB_UBO_MATCH_SCORE = 90;
    private static final long SIMULATED_DELAY_MS = 100;

    /** {@inheritDoc} */
    @Override
    public Mono<BusinessVerificationResult> verifyBusiness(UUID partyId, BusinessVerificationRequest request) {
        // TODO: Replace stub with real KYB provider (Onfido, Jumio, Veriff)
        log.debug("Stub KYB provider verifying business partyId={}", partyId);
        return Mono.delay(Duration.ofMillis(SIMULATED_DELAY_MS))
                .map(tick -> BusinessVerificationResult.builder()
                        .verified(true)
                        .score(STUB_BUSINESS_SCORE)
                        .riskLevel("LOW")
                        .details("Stub verification passed for business")
                        .provider(PROVIDER_NAME)
                        .build());
    }

    /** {@inheritDoc} */
    @Override
    public Mono<CorporateDocumentCheckResult> checkCorporateDocument(UUID documentId, CorporateDocumentCheckRequest request) {
        // TODO: Replace stub with real KYB provider (Onfido, Jumio, Veriff)
        log.debug("Stub KYB provider checking corporate document documentId={}", documentId);
        return Mono.delay(Duration.ofMillis(SIMULATED_DELAY_MS))
                .map(tick -> CorporateDocumentCheckResult.builder()
                        .valid(true)
                        .documentType(request.getDocumentType())
                        .confidence(STUB_DOCUMENT_CONFIDENCE)
                        .build());
    }

    /** {@inheritDoc} */
    @Override
    public Mono<UboVerificationResult> verifyUbo(UUID uboId, UboVerificationRequest request) {
        // TODO: Replace stub with real KYB provider (Onfido, Jumio, Veriff)
        log.debug("Stub KYB provider verifying UBO uboId={}", uboId);
        return Mono.delay(Duration.ofMillis(SIMULATED_DELAY_MS))
                .map(tick -> UboVerificationResult.builder()
                        .verified(true)
                        .matchScore(STUB_UBO_MATCH_SCORE)
                        .build());
    }

    /** {@inheritDoc} */
    @Override
    public Mono<VerificationStatusResult> getVerificationStatus(String externalReferenceId) {
        // TODO: Replace stub with real KYB provider (Onfido, Jumio, Veriff)
        log.debug("Stub KYB provider checking verification status externalReferenceId={}", externalReferenceId);
        return Mono.delay(Duration.ofMillis(SIMULATED_DELAY_MS))
                .map(tick -> VerificationStatusResult.builder()
                        .status("COMPLETED")
                        .externalId(externalReferenceId)
                        .completedAt(LocalDateTime.now())
                        .build());
    }
}
