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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import java.util.List;
import java.util.UUID;

/**
 * Submits a batch of authorized signers (Powers of Attorney) for the KYB
 * compliance case. Each signer is created individually via
 * {@code PowersOfAttorneyApi}.
 *
 * <p>Returns a {@link SubmissionResult} containing all created
 * power-of-attorney IDs, mirroring the {@link RegisterUbosCommand} pattern.</p>
 */
@Data
@Builder
public class SubmitAuthorizedSignersCommand implements Command<SubmissionResult> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;

    /**
     * The legal entity (party) the powers of attorney are attached to.
     * Maps to {@code PowerOfAttorneyDTO.partyId} on the SDK call.
     */
    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one authorized signer is required")
    private final List<SignerData> signers;
}
