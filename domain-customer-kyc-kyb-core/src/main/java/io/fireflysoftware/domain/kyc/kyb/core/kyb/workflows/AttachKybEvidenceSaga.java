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

import io.fireflysoftware.core.kycb.sdk.model.CorporateDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AddCorporateDocumentCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DeleteCorporateDocumentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.argument.Input;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static io.fireflysoftware.domain.kyc.kyb.core.kyb.constants.KybConstants.*;

/**
 * Saga that attaches corporate evidence (documents) to a KYB case.
 * Dispatches commands through the {@link CommandBus} to dedicated handlers.
 */
@Component
@Saga(name = SAGA_ATTACH_EVIDENCE)
@RequiredArgsConstructor
@Slf4j
public class AttachKybEvidenceSaga {

    private final CommandBus commandBus;

    /**
     * Uploads a corporate document to the compliance case.
     *
     * @param command the evidence details
     * @return the created corporate document DTO
     */
    @SagaStep(id = STEP_ATTACH_DOCUMENT, compensate = "deleteDocument", retry = 2, backoffMs = 1000)
    public Mono<CorporateDocumentDTO> attachDocument(@Input AttachEvidenceCommand command) {
        var cmd = AddCorporateDocumentCommand.builder().build();
        cmd.documentType(command.getDocumentType());
        cmd.documentReference(command.getDocumentName());

        log.info("Attaching KYB evidence: caseId={}, documentType={}", command.getCaseId(), command.getDocumentType());
        return commandBus.send(cmd);
    }

    /**
     * Compensation: deletes the attached document.
     */
    public Mono<Void> deleteDocument(CorporateDocumentDTO document) {
        if (document == null || document.getCorporateDocumentId() == null) {
            return Mono.empty();
        }
        log.warn("Compensating: deleting KYB evidence: documentId={}", document.getCorporateDocumentId());
        return commandBus.send(new DeleteCorporateDocumentCommand(document.getCorporateDocumentId()));
    }
}
