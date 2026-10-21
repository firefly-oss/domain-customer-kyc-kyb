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


package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for KYB compliance case data.
 * Used by both the create-case endpoint (only {@code caseId} populated) and
 * the get-case endpoint (all fields populated from the core service).
 */
@Data
@Builder
public class KybCaseResponse {

    private UUID caseId;
    private UUID partyId;
    private String caseType;
    private String caseStatus;
    private String casePriority;
    private String caseReference;
    private String caseSummary;
    private String assignedTo;
    private LocalDateTime dueDate;
    private LocalDateTime resolutionDate;
    private String resolutionNotes;
}
