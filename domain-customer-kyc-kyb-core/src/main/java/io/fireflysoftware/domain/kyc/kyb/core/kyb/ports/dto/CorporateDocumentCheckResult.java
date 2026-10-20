package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of a corporate document check from a third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateDocumentCheckResult {

    /** Whether the document is valid. */
    private boolean valid;

    /** The type of document that was checked. */
    private String documentType;

    /** Confidence score from 0 to 100. */
    private int confidence;
}
