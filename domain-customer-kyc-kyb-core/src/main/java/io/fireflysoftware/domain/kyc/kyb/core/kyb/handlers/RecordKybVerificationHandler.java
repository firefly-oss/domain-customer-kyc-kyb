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
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RecordKybVerificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that records a KYB verification result in the core layer via the SDK.
 */
@CommandHandlerComponent
public class RecordKybVerificationHandler extends CommandHandler<RecordKybVerificationCommand, KybVerificationDTO> {

    private final KybVerificationApi kybVerificationApi;

    public RecordKybVerificationHandler(KybVerificationApi kybVerificationApi) {
        this.kybVerificationApi = kybVerificationApi;
    }

    @Override
    protected Mono<KybVerificationDTO> doHandle(RecordKybVerificationCommand cmd) {
        var verificationDto = new KybVerificationDTO()
                .verificationStatus(cmd.getVerificationStatus());
        return kybVerificationApi.createKybVerification(cmd.getCaseId(), verificationDto, UUID.randomUUID().toString());
    }
}
