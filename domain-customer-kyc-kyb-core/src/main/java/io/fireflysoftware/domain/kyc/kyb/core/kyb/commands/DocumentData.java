package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Value object carrying corporate document data into the KYB workflow.
 * Passed inside {@link SubmitCorporateDocumentsCommand} and mapped to
 * {@code CorporateDocumentDTO} by {@code KybMapper} before the SDK call.
 */
@Data
@Builder
public class DocumentData {

    private String documentType;
    private String documentReference;
    private String documentSystemId;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String notaryName;
    private String notaryLocation;
    private String commercialRegistry;
    private String registryEntry;
}
