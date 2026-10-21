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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.services;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowInput;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface KybService {

    /**
     * Starts the full KYB workflow saga for an organization.
     * Creates the compliance case, uploads corporate documents, registers UBOs,
     * requests external verification, and evaluates the final result.
     *
     * @param input all data required by the 5-step KYB saga
     * @return Mono containing the saga result with per-step outcomes
     */
    Mono<SagaResult> startKyb(KybWorkflowInput input);
    
    /**
     * Attaches statutes, registries, shareholder records, and POA documents to a KYB case.
     * @param caseId the unique identifier of the KYB case
     * @param command the command containing document information to attach
     * @return Mono containing the result of attaching evidence
     */
    Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command);
    
    /**
     * Marks the KYB dossier as complete.
     * @param caseId the unique identifier of the KYB case
     * @return Mono containing the result of marking documents complete
     */
    Mono<SagaResult> markDocsComplete(UUID caseId);
    
    /**
     * Verifies entity (requires UBOs validated) and sets expiry.
     * @param caseId the unique identifier of the KYB case
     * @return Mono containing the result of KYB verification
     */
    Mono<SagaResult> verifyKyb(UUID caseId);
    
    /**
     * Rejects KYB due to opaque structure/sanctions/insufficient docs.
     * @param caseId the unique identifier of the KYB case
     * @return Mono containing the result of failing the KYB case
     */
    Mono<SagaResult> failKyb(UUID caseId);
    
    /**
     * Marks KYB as expired by time.
     * @param caseId the unique identifier of the KYB case
     * @return Mono containing the result of expiring the KYB case
     */
    Mono<SagaResult> expireKyb(UUID caseId);
    
    /**
     * Renews KYB level/expiry.
     * @param caseId the unique identifier of the KYB case
     * @return Mono containing the result of renewing the KYB case
     */
    Mono<SagaResult> renewKyb(UUID caseId);
}
