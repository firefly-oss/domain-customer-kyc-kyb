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

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DeleteKybCaseCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.argument.SetVariable;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.OnSagaComplete;
import org.fireflyframework.orchestration.saga.annotation.OnSagaError;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.kyb.constants.KybConstants.*;

/**
 * Saga that creates a new KYB compliance case in the core layer.
 * <p>
 * Single-step saga with compensation to cancel the created case on failure.
 * Dispatches commands through the {@link CommandBus} to dedicated handlers.
 * </p>
 */
@Component
@Saga(name = SAGA_START_KYB)
@RequiredArgsConstructor
@Slf4j
public class StartKybSaga {

    private final CommandBus commandBus;

    /**
     * Creates a new compliance case of type KYB.
     *
     * @return the created compliance case DTO
     */
    @SagaStep(id = STEP_CREATE_CASE, compensate = "cancelCase", retry = 2, backoffMs = 1000)
    @SetVariable(CTX_CASE_ID)
    public Mono<UUID> createCase() {
        var cmd = CreateKybCaseCommand.builder().build();

        log.info("Creating KYB compliance case");
        return commandBus.<UUID>send(cmd)
                .doOnNext(created -> log.info("KYB case created: caseId={}", created));
    }

    /**
     * Compensation: cancels the created compliance case.
     */
    public Mono<Void> cancelCase(UUID createdCaseId) {
        if (createdCaseId == null) {
            return Mono.empty();
        }
        log.warn("Compensating: cancelling KYB case: caseId={}", createdCaseId);
        return commandBus.send(new DeleteKybCaseCommand(createdCaseId));
    }

    @OnSagaComplete
    public void onComplete(ExecutionContext ctx, SagaResult result) {
        log.info("KYB case creation completed successfully");
    }

    @OnSagaError
    public void onError(Throwable error, ExecutionContext ctx) {
        log.error("KYB case creation failed: {}", error.getMessage());
    }
}
