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
