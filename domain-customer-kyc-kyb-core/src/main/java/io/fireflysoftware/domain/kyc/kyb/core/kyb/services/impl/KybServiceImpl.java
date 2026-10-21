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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.services.impl;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.services.KybService;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowInput;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implements the KYB domain service by delegating saga orchestration to
 * {@link SagaEngine} and individual lifecycle transitions to the command bus
 * (future implementation via dedicated command handlers).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KybServiceImpl implements KybService {

    private final SagaEngine sagaEngine;

    @Override
    public Mono<SagaResult> startKyb(KybWorkflowInput input) {
        log.info("Starting KYB workflow for partyId={}", input.partyId());
        StepInputs inputs = StepInputs.builder()
                .forStepId(KybWorkflowConstants.STEP_CREATE_CASE, input)
                .build();
        return sagaEngine.execute(KybWorkflowConstants.SAGA_NAME, inputs);
    }

    @Override
    public Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command) {
        return Mono.empty();
    }

    @Override
    public Mono<SagaResult> markDocsComplete(UUID caseId) {
        return Mono.empty();
    }

    @Override
    public Mono<SagaResult> verifyKyb(UUID caseId) {
        return Mono.empty();
    }

    @Override
    public Mono<SagaResult> failKyb(UUID caseId) {
        return Mono.empty();
    }

    @Override
    public Mono<SagaResult> expireKyb(UUID caseId) {
        return Mono.empty();
    }

    @Override
    public Mono<SagaResult> renewKyb(UUID caseId) {
        return Mono.empty();
    }
}
