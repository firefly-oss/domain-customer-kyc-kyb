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
