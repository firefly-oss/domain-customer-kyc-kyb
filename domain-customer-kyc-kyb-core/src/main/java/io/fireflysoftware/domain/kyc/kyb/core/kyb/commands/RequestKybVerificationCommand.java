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

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;

import java.util.UUID;

/**
 * Triggers external KYB verification via {@code KybVerificationPort},
 * persists the result as a {@code KybVerificationDTO} in the core service,
 * and returns the full verification DTO so the saga step can inspect the
 * outcome and store verification status in context.
 */
@Data
@Builder
public class RequestKybVerificationCommand implements Command<KybVerificationDTO> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;

    @NotNull(message = "Party ID is required")
    private final UUID partyId;
}
