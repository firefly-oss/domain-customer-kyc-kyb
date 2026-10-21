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
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.VerifyKybCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles approving a KYB verification by updating its status to VERIFIED
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class UpdateKybStatusHandler extends CommandHandler<VerifyKybCommand, Void> {

    private final KybVerificationApi kybVerificationApi;

    /**
     * Updates the KYB verification status to VERIFIED.
     *
     * @param cmd the verify command containing caseId and partyId
     * @return an empty Mono on success
     */
    @Override
    protected Mono<Void> doHandle(VerifyKybCommand cmd) {
        KybVerificationDTO updateDto = new KybVerificationDTO()
                .verificationStatus(STATUS_VERIFIED)
                .verificationDate(LocalDateTime.now());

        return kybVerificationApi.updateKybVerification(
                        cmd.getPartyId(), cmd.getCaseId(), updateDto, UUID.randomUUID().toString())
                .then();
    }
}
