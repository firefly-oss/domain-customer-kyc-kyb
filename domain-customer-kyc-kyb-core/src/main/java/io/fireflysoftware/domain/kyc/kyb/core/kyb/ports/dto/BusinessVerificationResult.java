package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a business verification from a third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResult {

    /** Whether the business passed verification. */
    private boolean verified;

    /** Confidence score from 0 to 100. */
    private int score;

    /** Risk level determined by the provider. */
    private String riskLevel;

    /** Additional details from the provider. */
    private String details;

    /** The provider that performed the verification. */
    private String provider;
}
