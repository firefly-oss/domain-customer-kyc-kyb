package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to approve and verify a KYB case (requires UBOs validated).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyKybCommand implements Command<Void> {

    /** The identifier of the KYB case to verify. */
    @NotNull
    private UUID caseId;

    /** The identifier of the organization party undergoing verification. */
    @NotNull
    private UUID partyId;
}
