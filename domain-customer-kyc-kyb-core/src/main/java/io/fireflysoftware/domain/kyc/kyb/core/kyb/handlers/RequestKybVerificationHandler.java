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

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RequestKybVerificationCommand;
import io.fireflysoftware.domain.kyc.kyb.infra.KybVerificationPort;
import io.fireflysoftware.domain.kyc.kyb.infra.KybVerificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants.VERIFICATION_STATUS_IN_PROGRESS;
import static io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants.VERIFICATION_STATUS_REJECTED;
import static io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants.VERIFICATION_STATUS_VERIFIED;

/**
 * Orchestrates the KYB verification lifecycle:
 * <ol>
 *   <li>Delegates to {@link KybVerificationPort} for external provider verification</li>
 *   <li>Persists the result as a {@code KybVerificationDTO} via {@code KybVerificationApi}</li>
 * </ol>
 * Returns the full {@code KybVerificationDTO} so the saga step can inspect the
 * outcome and store the verification status and ID in context.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent(timeout = 60000, retries = 1, metrics = true, tracing = true)
public class RequestKybVerificationHandler extends CommandHandler<RequestKybVerificationCommand, KybVerificationDTO> {

    private final KybVerificationPort verificationPort;
    private final KybVerificationApi kybVerificationApi;

    @Override
    protected Mono<KybVerificationDTO> doHandle(RequestKybVerificationCommand cmd) {
        log.info("Requesting KYB verification for caseId={}, partyId={}", cmd.getCaseId(), cmd.getPartyId());

        return verificationPort.verify(cmd.getCaseId())
                .flatMap(result -> persistVerification(cmd, result))
                .doOnSuccess(dto -> log.info(
                        "KYB verification persisted: verificationId={}, caseId={}, status={}",
                        dto.getKybVerificationId(), cmd.getCaseId(), dto.getVerificationStatus()));
    }

    private Mono<KybVerificationDTO> persistVerification(RequestKybVerificationCommand cmd,
                                                          KybVerificationResult result) {
        String status = "VERIFIED".equals(result.getStatus())
                ? VERIFICATION_STATUS_VERIFIED
                : VERIFICATION_STATUS_REJECTED;

        KybVerificationDTO dto = new KybVerificationDTO();
        dto.partyId(cmd.getPartyId());
        dto.verificationStatus(status);
        dto.verificationDate(LocalDateTime.now());
        dto.mercantileRegistryVerified(false);
        dto.deedOfIncorporationVerified(false);
        dto.businessStructureVerified(false);
        dto.uboVerified(false);
        dto.taxIdVerified(false);
        dto.operatingLicenseVerified(false);
        dto.riskScore(result.getRiskScore() != null ? result.getRiskScore() : 0);
        dto.riskLevel("MEDIUM");
        dto.nextReviewDate(LocalDateTime.now().plusYears(1));

        if (VERIFICATION_STATUS_REJECTED.equals(status)) {
            dto.verificationNotes(result.getRejectionReason());
        }

        return kybVerificationApi.createKybVerification(
                cmd.getPartyId(), dto, UUID.randomUUID().toString());
    }
}
