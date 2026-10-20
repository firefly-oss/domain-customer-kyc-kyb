package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.kyb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO for KYB case operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KybCaseResponse {

    private UUID caseId;
    private String status;
    private String message;
}
