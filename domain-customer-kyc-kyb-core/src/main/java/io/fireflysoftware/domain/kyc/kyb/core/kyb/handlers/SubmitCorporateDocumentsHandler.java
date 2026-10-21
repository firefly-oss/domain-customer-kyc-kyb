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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.CorporateDocumentsApi;
import io.fireflysoftware.core.kycb.sdk.model.CorporateDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmitCorporateDocumentsCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.mappers.KybMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * Uploads each corporate document in the command to {@code CorporateDocumentsApi}.
 * Documents are processed concurrently (Flux.flatMap with unbounded concurrency).
 * Returns a {@link SubmissionResult} containing all created document IDs.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent(timeout = 60000, retries = 2, metrics = true, tracing = true)
public class SubmitCorporateDocumentsHandler extends CommandHandler<SubmitCorporateDocumentsCommand, SubmissionResult> {

    private final CorporateDocumentsApi corporateDocumentsApi;
    private final KybMapper kybMapper;

    @Override
    protected Mono<SubmissionResult> doHandle(SubmitCorporateDocumentsCommand cmd) {
        log.info("Submitting {} corporate document(s) for caseId={}", cmd.getDocuments().size(), cmd.getCaseId());

        return Flux.fromIterable(cmd.getDocuments())
                .flatMap(doc -> uploadDocument(doc, cmd.getPartyId()))
                .collectList()
                .map(SubmissionResult::new)
                .doOnSuccess(result ->
                        log.info("Submitted {} document(s) for caseId={}", result.ids().size(), cmd.getCaseId()));
    }

    private Mono<UUID> uploadDocument(DocumentData doc, UUID partyId) {
        CorporateDocumentDTO dto = kybMapper.toDto(doc);
        dto.partyId(partyId);

        return corporateDocumentsApi
                .addCorporateDocument(dto, UUID.randomUUID().toString())
                .map(result -> Objects.requireNonNull(result.getCorporateDocumentId(),
                        "Core service returned null corporateDocumentId"));
    }
}
