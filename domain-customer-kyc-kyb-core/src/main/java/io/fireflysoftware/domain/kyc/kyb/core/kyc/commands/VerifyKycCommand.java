package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to approve and verify a KYC case at the specified level.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyKycCommand implements Command<Void> {

    /** The identifier of the KYC case to verify. */
    @NotNull
    private UUID caseId;

    /** The identifier of the party undergoing verification. */
    @NotNull
    private UUID partyId;

    /** The verification level to approve at. */
    private String verificationLevel;
}
