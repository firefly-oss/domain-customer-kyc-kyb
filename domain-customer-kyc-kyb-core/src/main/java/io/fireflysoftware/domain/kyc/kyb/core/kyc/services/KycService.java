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


package io.fireflysoftware.domain.kyc.kyb.core.kyc.services;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.OpenKycCaseCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface KycService {
    
    /**
     * Opens a new KYC case with required due diligence level.
     * @return Mono containing the result of opening the KYC case
     */
    Mono<SagaResult> openCase(OpenKycCaseCommand command);

    Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command);

    Mono<SagaResult> markDocsComplete(UUID caseId, UUID partyId);

    Mono<SagaResult> recordPepAdverseMedia(UUID caseId, UUID partyId);

    Mono<SagaResult> verify(UUID caseId, UUID partyId);

    Mono<SagaResult> fail(UUID caseId, UUID partyId);

    Mono<SagaResult> expire(UUID caseId, UUID partyId);

    Mono<SagaResult> renew(UUID caseId, UUID partyId);
}
