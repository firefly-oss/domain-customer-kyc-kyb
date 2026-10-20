package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Opens a new KYB compliance case in {@code core-common-kycb-mgmt}.
 * Returns the created {@code complianceCaseId}.
 */
@Data
@Builder
public class CreateKybCaseCommand implements Command<UUID> {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotBlank(message = "Business name is required")
    private final String businessName;

    @NotBlank(message = "Registration number is required")
    private final String registrationNumber;

    @NotNull(message = "Tenant ID is required")
    private final UUID tenantId;
}
