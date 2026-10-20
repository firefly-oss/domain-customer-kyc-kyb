package io.fireflysoftware.domain.kyc.kyb.core.kyb.queries;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.query.Query;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;

import java.util.UUID;

/**
 * Retrieves the KYB verification result by party and verification identifiers.
 *
 * <p>Both IDs are stored in {@code SagaContext} after the
 * {@code requestVerification} step completes and can be used by downstream
 * consumers to fetch the final verification outcome.</p>
 */
@Data
@Builder
public class GetKybResultQuery implements Query<KybVerificationDTO> {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotNull(message = "Verification ID is required")
    private final UUID verificationId;

    @Override
    public String getCacheKey() {
        return "KybResult:" + partyId + ":" + verificationId;
    }
}
