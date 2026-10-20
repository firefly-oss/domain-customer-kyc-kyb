package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a UBO verification from a third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UboVerificationResult {

    /** Whether the UBO identity was verified. */
    private boolean verified;

    /** Match score from 0 to 100. */
    private int matchScore;
}
