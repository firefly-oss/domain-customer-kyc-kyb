package io.fireflysoftware.domain.kyc.kyb.infra;

import lombok.Builder;
import lombok.Data;

/**
 * Result returned by a {@link KybVerificationPort} implementation.
 * Use the static factory methods for the two most common outcomes.
 */
@Data
@Builder
public class KybVerificationResult {

    /** Verification outcome. Typically {@code "VERIFIED"} or {@code "REJECTED"}. */
    private String status;

    /** Human-readable rejection reason. Populated only when {@link #status} is {@code "REJECTED"}. */
    private String rejectionReason;

    /** Provider risk score (0–100). May be null if the provider does not supply a score. */
    private Integer riskScore;

    /**
     * Creates a successful verification result with status {@code "VERIFIED"}.
     *
     * @return a verified result
     */
    public static KybVerificationResult verified() {
        return KybVerificationResult.builder()
                .status("VERIFIED")
                .build();
    }

    /**
     * Creates a rejected verification result with the given reason.
     *
     * @param reason human-readable rejection reason
     * @return a rejected result
     */
    public static KybVerificationResult rejected(String reason) {
        return KybVerificationResult.builder()
                .status("REJECTED")
                .rejectionReason(reason)
                .build();
    }
}
