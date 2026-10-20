package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to reject/fail a KYC case due to fraud, inconsistency, or other reasons.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FailKycCommand implements Command<Void> {

    /** The identifier of the KYC case to fail. */
    @NotNull
    private UUID caseId;

    /** The identifier of the party undergoing verification. */
    @NotNull
    private UUID partyId;

    /** The reason for rejecting the KYC case. */
    @NotBlank
    private String reason;
}
