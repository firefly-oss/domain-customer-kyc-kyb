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
 * CQRS command to open a new KYC verification case for a party.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenKycCaseCommand implements Command<UUID> {

    /** The identifier of the party undergoing KYC verification. */
    @NotNull
    private UUID partyId;

    /** The required due-diligence level (e.g. STANDARD, ENHANCED). */
    @NotBlank
    private String dueDiligenceLevel;
}
