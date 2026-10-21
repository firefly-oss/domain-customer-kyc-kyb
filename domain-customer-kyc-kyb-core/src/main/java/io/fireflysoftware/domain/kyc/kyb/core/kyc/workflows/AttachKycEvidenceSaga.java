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


package io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachKycEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.DeleteKycEvidenceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Saga that orchestrates attaching evidence documents to an existing KYC case.
 * Uploads the document via the command bus and stores the resulting document ID
 * in the execution context.
 */
@Saga(name = SAGA_ATTACH_KYC_EVIDENCE)
@Service
@Slf4j
@RequiredArgsConstructor
public class AttachKycEvidenceSaga {

    private final CommandBus commandBus;

    /**
     * Uploads a document for a KYC case and stores its identifier in the execution context.
     *
     * @param cmd the command containing document metadata
     * @param ctx the saga execution context
     * @return a Mono emitting the newly created document identifier
     */
    @SagaStep(id = STEP_UPLOAD_DOCUMENT, compensate = COMPENSATE_DELETE_DOCUMENT)
    @StepEvent(type = EVENT_KYC_EVIDENCE_ATTACHED)
    public Mono<UUID> uploadDocument(AttachKycEvidenceCommand cmd, ExecutionContext ctx) {
        return commandBus.<UUID>send(cmd)
                .doOnNext(documentId -> {
                    ctx.putVariable(CTX_DOCUMENT_ID, documentId);
                    ctx.putVariable("partyId", cmd.getPartyId());
                    ctx.putVariable("caseId", cmd.getCaseId());
                });
    }

    /**
     * Deletes the evidence document the step just stored.
     */
    public Mono<Void> compensateDeleteDocument(UUID documentId, ExecutionContext ctx) {
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        UUID caseId = ctx.getVariableAs("caseId", UUID.class);
        if (documentId == null || partyId == null || caseId == null) {
            return Mono.empty();
        }
        return commandBus.send(new DeleteKycEvidenceCommand(partyId, caseId, documentId));
    }
}
