package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UboData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request body for registering Ultimate Beneficial Owners against a KYB case.
 * The {@code caseId} is taken from the path variable.
 */
@Data
@Builder
public class RegisterUbosRequest {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one UBO is required")
    private final List<UboData> ubos;
}
