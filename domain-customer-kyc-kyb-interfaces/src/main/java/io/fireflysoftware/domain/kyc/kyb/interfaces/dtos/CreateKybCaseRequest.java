package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Request body for creating a new KYB compliance case for a legal entity.
 */
@Data
@Builder
public class CreateKybCaseRequest {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotBlank(message = "Business name is required")
    private final String businessName;

    @NotBlank(message = "Registration number is required")
    private final String registrationNumber;

    @NotNull(message = "Tenant ID is required")
    private final UUID tenantId;
}
