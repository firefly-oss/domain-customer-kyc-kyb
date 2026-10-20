package io.fireflysoftware.domain.kyc.kyb.infra;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Port for external KYB (Know Your Business) verification providers.
 * Implementations may delegate to Sumsub, Onfido, Dow Jones, or a stub.
 *
 * <p>Use {@link StubKybVerificationAdapter} for local development and testing.</p>
 */
public interface KybVerificationPort {

    /**
     * Submits a KYB verification request for the given case identifier.
     *
     * @param caseId the KYB compliance case identifier
     * @return a {@link Mono} emitting the verification result
     */
    Mono<KybVerificationResult> verify(UUID caseId);
}
