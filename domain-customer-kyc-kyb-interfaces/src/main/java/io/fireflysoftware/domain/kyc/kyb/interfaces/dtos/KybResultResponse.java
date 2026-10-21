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
 * Response DTO for KYB verification results.
 * Returned after requesting verification ({@code POST /cases/{caseId}/verify})
 * and querying the final outcome ({@code GET /cases/{caseId}/result}).
 */
@Data
@Builder
public class KybResultResponse {

    private UUID verificationId;
    private UUID partyId;
    private String verificationStatus;
    private Integer riskScore;
    private String riskLevel;
    private LocalDateTime verificationDate;
    private LocalDateTime nextReviewDate;
    private Boolean mercantileRegistryVerified;
    private Boolean deedOfIncorporationVerified;
    private Boolean businessStructureVerified;
    private Boolean uboVerified;
    private Boolean taxIdVerified;
    private Boolean operatingLicenseVerified;
    private String verificationNotes;
}
