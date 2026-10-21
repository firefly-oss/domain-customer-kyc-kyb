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

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationDocumentsApi;
import io.fireflysoftware.core.kycb.sdk.model.VerificationDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachKycEvidenceCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles attaching a verification document to an existing KYC case
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class UploadKycDocumentHandler extends CommandHandler<AttachKycEvidenceCommand, UUID> {

    private final KycVerificationDocumentsApi kycVerificationDocumentsApi;

    /**
     * Uploads a verification document and links it to the KYC case.
     *
     * @param cmd the attach-evidence command containing document metadata
     * @return a Mono emitting the newly created document identifier
     */
    @Override
    protected Mono<UUID> doHandle(AttachKycEvidenceCommand cmd) {
        VerificationDocumentDTO docDto = new VerificationDocumentDTO()
                .kycVerificationId(cmd.getCaseId())
                .documentType(cmd.getDocumentType())
                .documentReference(cmd.getFingerprint())
                .verificationPurpose("IDENTITY")
                .isVerified(false);

        return kycVerificationDocumentsApi.addVerificationDocument(
                        cmd.getPartyId(), cmd.getCaseId(), docDto, UUID.randomUUID().toString())
                .map(VerificationDocumentDTO::getVerificationDocumentId);
    }
}
