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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RecordKybVerificationCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RequestKybVerificationCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.argument.FromStep;
import org.fireflyframework.orchestration.core.argument.Input;
import org.fireflyframework.orchestration.core.argument.SetVariable;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.OnSagaComplete;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.kyb.constants.KybConstants.*;

/**
 * Saga that requests KYB verification through the external provider
 * and evaluates the result.
 * <p>
 * Step 1: Call external KYB verification provider via {@link CommandBus}
 * Step 2: Evaluate result and update verification status in core via {@link CommandBus}
 * </p>
 */
@Component
@Saga(name = SAGA_VERIFY_KYB)
@RequiredArgsConstructor
@Slf4j
public class VerifyKybSaga {

    private final CommandBus commandBus;

    /**
     * Requests verification from the external KYB provider.
     *
     * @param caseId the compliance case identifier
     * @param partyId the party identifier
     * @return the verification result from the external provider
     */
    @SagaStep(id = STEP_REQUEST_VERIFICATION, compensate = "cancelVerification",
            retry = 2, backoffMs = 2000, timeoutMs = 30000)
    public Mono<KybVerificationDTO> requestVerification(@Input UUID caseId, @Input UUID partyId) {
        log.info("Requesting KYB verification: caseId={}", caseId);
        return commandBus.send(RequestKybVerificationCommand.builder()
                .caseId(caseId)
                .partyId(partyId)
                .build());
    }

    /**
     * Evaluates the verification result and updates the core KYB verification record.
     *
     * @param caseId the compliance case identifier
     * @param result the external verification result
     * @return the KYB verification DTO from core
     */
    @SagaStep(id = STEP_EVALUATE_RESULT, dependsOn = STEP_REQUEST_VERIFICATION)
    @SetVariable(CTX_VERIFICATION_STATUS)
    public Mono<KybVerificationDTO> evaluateResult(
            @Input UUID caseId,
            @FromStep(STEP_REQUEST_VERIFICATION) KybVerificationDTO result) {
        log.info("Evaluating KYB verification result: caseId={}, status={}", caseId, result.getVerificationStatus());
        return commandBus.send(new RecordKybVerificationCommand(caseId, result.getVerificationStatus()));
    }

    /**
     * Compensation: cancels the pending verification.
     */
    public Mono<Void> cancelVerification(KybVerificationDTO result) {
        log.warn("Compensating: cancelling KYB verification");
        return Mono.empty();
    }

    @OnSagaComplete
    public void onComplete(ExecutionContext ctx, SagaResult sagaResult) {
        log.info("KYB verification saga completed");
    }
}
