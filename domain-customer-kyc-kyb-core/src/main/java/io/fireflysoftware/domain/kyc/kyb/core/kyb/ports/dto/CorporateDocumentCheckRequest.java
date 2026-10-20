package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for corporate document verification via third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateDocumentCheckRequest {

    /** The document identifier. */
    private UUID documentId;

    /** The type of corporate document. */
    private String documentType;

    /** The document content (base64 encoded). */
    private String content;
}
