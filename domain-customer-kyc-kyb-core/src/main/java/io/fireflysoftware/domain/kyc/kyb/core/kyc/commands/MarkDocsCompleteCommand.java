package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to mark a KYC case's document dossier as complete for review.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarkDocsCompleteCommand implements Command<Void> {

    /** The identifier of the KYC case whose dossier is complete. */
    @NotNull
    private UUID caseId;

    /** The identifier of the party undergoing verification. */
    @NotNull
    private UUID partyId;
}
