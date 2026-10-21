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


package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KycVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.FailKycCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles rejecting a KYC verification by updating its status to FAILED
 * with a rejection reason via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class FailKycHandler extends CommandHandler<FailKycCommand, Void> {

    private final KycVerificationApi kycVerificationApi;

    /**
     * Updates the KYC verification status to FAILED with the given rejection reason.
     *
     * @param cmd the fail command containing caseId, partyId, and reason
     * @return an empty Mono on success
     */
    @Override
    protected Mono<Void> doHandle(FailKycCommand cmd) {
        KycVerificationDTO updateDto = new KycVerificationDTO()
                .verificationStatus(STATUS_FAILED)
                .rejectionReason(cmd.getReason())
                .verificationDate(LocalDateTime.now());

        return kycVerificationApi.updateKycVerification(
                        cmd.getPartyId(), cmd.getCaseId(), updateDto, UUID.randomUUID().toString())
                .then();
    }
}
