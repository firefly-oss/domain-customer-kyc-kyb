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
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UpdateCaseStatusCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that updates the status of a KYB compliance case via the core SDK.
 */
@CommandHandlerComponent
public class UpdateCaseStatusHandler extends CommandHandler<UpdateCaseStatusCommand, ComplianceCaseDTO> {

    private final ComplianceCasesApi complianceCasesApi;

    public UpdateCaseStatusHandler(ComplianceCasesApi complianceCasesApi) {
        this.complianceCasesApi = complianceCasesApi;
    }

    @Override
    protected Mono<ComplianceCaseDTO> doHandle(UpdateCaseStatusCommand cmd) {
        return complianceCasesApi.updateComplianceCase(
                cmd.getCaseId(),
                new ComplianceCaseDTO().caseStatus(cmd.getNewStatus()), UUID.randomUUID().toString());
    }
}
