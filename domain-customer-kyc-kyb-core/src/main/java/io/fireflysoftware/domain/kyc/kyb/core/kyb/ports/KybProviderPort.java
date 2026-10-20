package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.BusinessVerificationRequest;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.BusinessVerificationResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.CorporateDocumentCheckRequest;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.CorporateDocumentCheckResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.UboVerificationRequest;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.UboVerificationResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.VerificationStatusResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Port interface for third-party KYB verification providers.
 *
 * <p>Implementations connect to external KYB providers such as Onfido, Jumio, or Veriff
 * to perform business verification, corporate document checks, and UBO verification.
 * A stub implementation is provided for development and testing.
 */
public interface KybProviderPort {

    /**
     * Verifies a business entity against the external provider.
     *
     * @param partyId the party identifier of the business
     * @param request the verification request with business details
     * @return verification result with score and risk level
     */
    Mono<BusinessVerificationResult> verifyBusiness(UUID partyId, BusinessVerificationRequest request);

    /**
     * Checks a corporate document against the external provider.
     *
     * @param documentId the document identifier
     * @param request the document check request
     * @return document check result with validity and confidence
     */
    Mono<CorporateDocumentCheckResult> checkCorporateDocument(UUID documentId, CorporateDocumentCheckRequest request);

    /**
     * Verifies an Ultimate Beneficial Owner against the external provider.
     *
     * @param uboId the UBO identifier
     * @param request the UBO verification request
     * @return UBO verification result with match score
     */
    Mono<UboVerificationResult> verifyUbo(UUID uboId, UboVerificationRequest request);

    /**
     * Retrieves the status of an ongoing verification from the external provider.
     *
     * @param externalReferenceId the external provider's reference identifier
     * @return the current verification status
     */
    Mono<VerificationStatusResult> getVerificationStatus(String externalReferenceId);
}
