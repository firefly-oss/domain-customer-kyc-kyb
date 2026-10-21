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

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UpdateCaseStatusCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Saga(name = ChangeKybCaseStatusSaga.NAME)
@Service
public class ChangeKybCaseStatusSaga {

    public static final String NAME = "ChangeKybCaseStatusSaga";
    public static final String STEP = "updateStatus";

    private final CommandBus commandBus;
    private final ComplianceCasesApi complianceCasesApi;

    public ChangeKybCaseStatusSaga(CommandBus commandBus, ComplianceCasesApi complianceCasesApi) {
        this.commandBus = commandBus;
        this.complianceCasesApi = complianceCasesApi;
    }

    @SagaStep(id = STEP, compensate = "restoreStatus")
    @StepEvent(type = "kyb.case.status.updated")
    public Mono<ComplianceCaseDTO> updateStatus(UpdateCaseStatusCommand command, ExecutionContext ctx) {
        if (command.getCaseId() != null) {
            ctx.putVariable("caseId", command.getCaseId());
        }
        return complianceCasesApi.getComplianceCase(command.getCaseId(), UUID.randomUUID().toString())
                .doOnNext(current -> {
                    if (current.getCaseStatus() != null) {
                        ctx.putVariable("previousStatus", current.getCaseStatus());
                    }
                })
                .then(commandBus.send(command));
    }

    public Mono<Void> restoreStatus(ComplianceCaseDTO updated, ExecutionContext ctx) {
        UUID caseId = ctx.getVariableAs("caseId", UUID.class);
        String previous = ctx.getVariableAs("previousStatus", String.class);
        if (caseId == null || previous == null) {
            return Mono.empty();
        }
        return commandBus.send(new UpdateCaseStatusCommand(caseId, previous)).then();
    }
}
