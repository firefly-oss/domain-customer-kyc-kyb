package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import java.util.List;
import java.util.UUID;

/**
 * Uploads a batch of corporate documents to the KYB compliance case.
 * Each document is created individually via {@code CorporateDocumentsApi}.
 * Returns a {@link SubmissionResult} containing all created document IDs.
 */
@Data
@Builder
public class SubmitCorporateDocumentsCommand implements Command<SubmissionResult> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one document is required")
    private final List<DocumentData> documents;
}
