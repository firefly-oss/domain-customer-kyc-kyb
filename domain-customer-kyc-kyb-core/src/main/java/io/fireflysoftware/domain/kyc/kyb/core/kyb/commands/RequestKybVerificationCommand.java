package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;

import java.util.UUID;

/**
 * Triggers external KYB verification via {@code KybVerificationPort},
 * persists the result as a {@code KybVerificationDTO} in the core service,
 * and returns the full verification DTO so the saga step can inspect the
 * outcome and store verification status in context.
 */
@Data
@Builder
public class RequestKybVerificationCommand implements Command<KybVerificationDTO> {

    @NotNull(message = "Case ID is required")
    private final UUID caseId;

    @NotNull(message = "Party ID is required")
    private final UUID partyId;
}
