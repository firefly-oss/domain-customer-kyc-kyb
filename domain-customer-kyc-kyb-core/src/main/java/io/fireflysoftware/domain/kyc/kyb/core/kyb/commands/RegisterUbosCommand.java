package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import java.util.List;
import java.util.UUID;

/**
 * Registers a batch of Ultimate Beneficial Owners for the KYB compliance case.
 * Each UBO is created individually via {@code UboManagementApi}.
 * Returns a {@link SubmissionResult} containing all created UBO IDs.
 */
@Data
@Builder
public class RegisterUbosCommand implements Command<SubmissionResult> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one UBO is required")
    private final List<UboData> ubos;
}
