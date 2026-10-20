package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * CQRS command to open a new KYB verification case for an organization.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenKybCaseCommand implements Command<UUID> {

    /** The identifier of the organization party undergoing KYB verification. */
    @NotNull
    private UUID partyId;
}
