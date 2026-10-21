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

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Value object carrying corporate document data into the KYB workflow.
 * Passed inside {@link SubmitCorporateDocumentsCommand} and mapped to
 * {@code CorporateDocumentDTO} by {@code KybMapper} before the SDK call.
 */
@Data
@Builder
public class DocumentData {

    private String documentType;
    private String documentReference;
    private String documentSystemId;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String notaryName;
    private String notaryLocation;
    private String commercialRegistry;
    private String registryEntry;
}
