package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request body for uploading a batch of corporate documents to a KYB case.
 * The {@code caseId} is taken from the path variable; {@code partyId} is
 * provided here to authorise the upload against the owning party.
 */
@Data
@Builder
public class SubmitCorporateDocumentsRequest {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one document is required")
    private final List<DocumentData> documents;
}
