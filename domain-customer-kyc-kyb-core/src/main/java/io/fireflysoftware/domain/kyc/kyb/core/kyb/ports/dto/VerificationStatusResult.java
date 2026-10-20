package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of a verification status query from a third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationStatusResult {

    /** The current verification status. */
    private String status;

    /** The external provider's reference identifier. */
    private String externalId;

    /** The timestamp when the verification was completed, if applicable. */
    private LocalDateTime completedAt;
}
