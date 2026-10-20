package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for KYB compliance case data.
 * Used by both the create-case endpoint (only {@code caseId} populated) and
 * the get-case endpoint (all fields populated from the core service).
 */
@Data
@Builder
public class KybCaseResponse {

    private UUID caseId;
    private UUID partyId;
    private String caseType;
    private String caseStatus;
    private String casePriority;
    private String caseReference;
    private String caseSummary;
    private String assignedTo;
    private LocalDateTime dueDate;
    private LocalDateTime resolutionDate;
    private String resolutionNotes;
}
