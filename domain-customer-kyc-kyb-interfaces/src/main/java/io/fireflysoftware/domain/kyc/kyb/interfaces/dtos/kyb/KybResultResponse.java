package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.kyb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for KYB verification result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KybResultResponse {

    private UUID caseId;
    private String verificationStatus;
    private boolean success;
    private String message;
}
