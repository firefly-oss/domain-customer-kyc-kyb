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
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.VerifyKycCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles approving a KYC verification by updating its status to VERIFIED
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class UpdateVerificationStatusHandler extends CommandHandler<VerifyKycCommand, Void> {

    private final KycVerificationApi kycVerificationApi;

    /**
     * Updates the KYC verification status to VERIFIED.
     *
     * @param cmd the verify command containing caseId and partyId
     * @return an empty Mono on success
     */
    @Override
    protected Mono<Void> doHandle(VerifyKycCommand cmd) {
        KycVerificationDTO updateDto = new KycVerificationDTO()
                .verificationStatus(STATUS_VERIFIED)
                .verificationDate(LocalDateTime.now());

        return kycVerificationApi.updateKycVerification(
                        cmd.getPartyId(), cmd.getCaseId(), updateDto, UUID.randomUUID().toString())
                .then();
    }
}
