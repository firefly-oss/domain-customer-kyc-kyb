package io.fireflysoftware.domain.kyc.kyb.core.kyb.queries;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.query.Query;

import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;

import java.util.UUID;

/**
 * Retrieves a KYB compliance case by its unique identifier.
 * Handled by {@code GetKybCaseHandler} via {@code ComplianceCasesApi}.
 */
@Data
@Builder
public class GetKybCaseQuery implements Query<ComplianceCaseDTO> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;
}
