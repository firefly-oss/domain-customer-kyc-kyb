package com.firefly.domain.kyc.kyb.interfaces.dtos;

import com.firefly.domain.kyc.kyb.core.kyb.commands.SignerData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request body for submitting authorized signers (Powers of Attorney)
 * against a KYB case. The {@code caseId} is taken from the path variable.
 */
@Data
@Builder
public class SubmitAuthorizedSignersRequest {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one authorized signer is required")
    private final List<SignerData> signers;
}
