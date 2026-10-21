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

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Creates a KYB compliance case in {@code core-common-kycb-mgmt} and returns
 * the generated {@code complianceCaseId}.
 *
 * <p>The compliance case acts as the root aggregate for the entire KYB workflow.
 * All documents, UBOs and verification records reference it by ID.</p>
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent(timeout = 30000, retries = 2, metrics = true, tracing = true)
public class CreateKybCaseHandler extends CommandHandler<CreateKybCaseCommand, UUID> {

    private final ComplianceCasesApi complianceCasesApi;

    @Override
    protected Mono<UUID> doHandle(CreateKybCaseCommand cmd) {
        log.info("Creating KYB compliance case for partyId={}", cmd.getPartyId());

        ComplianceCaseDTO dto = new ComplianceCaseDTO();
        dto.partyId(cmd.getPartyId());
        dto.caseType(KybWorkflowConstants.CASE_TYPE_KYB);
        dto.caseStatus(KybWorkflowConstants.CASE_STATUS_OPEN);
        dto.casePriority("MEDIUM");
        dto.caseReference(cmd.getRegistrationNumber());
        dto.caseSummary(cmd.getBusinessName());
        dto.assignedTo("SYSTEM");
        dto.dueDate(LocalDateTime.now().plusDays(30));
        dto.reportToSepblacRequired(false);

        return complianceCasesApi
                .createComplianceCase(dto, UUID.randomUUID().toString())
                .map(result -> {
                    UUID caseId = Objects.requireNonNull(result.getComplianceCaseId(),
                            "Core service returned null complianceCaseId");
                    log.info("KYB compliance case created: caseId={}, partyId={}", caseId, cmd.getPartyId());
                    return caseId;
                });
    }
}
