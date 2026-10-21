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


package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to reject/fail a KYC case due to fraud, inconsistency, or other reasons.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailKycCommand implements Command<Void> {

    /** The identifier of the KYC case to fail. */
    @NotNull
    private UUID caseId;

    /** The identifier of the party undergoing verification. */
    @NotNull
    private UUID partyId;

    /** The reason for rejecting the KYC case. */
    @NotBlank
    private String reason;
}
