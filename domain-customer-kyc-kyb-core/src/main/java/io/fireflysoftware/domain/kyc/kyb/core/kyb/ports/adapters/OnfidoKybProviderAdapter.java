package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.adapters;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.KybProviderPort;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Onfido implementation of {@link KybProviderPort}.
 *
 * <p>TODO: Implement real Onfido integration for production KYB verification.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "kyb.provider", havingValue = "onfido")
public class OnfidoKybProviderAdapter implements KybProviderPort {

    /** {@inheritDoc} */
    @Override
    public Mono<BusinessVerificationResult> verifyBusiness(UUID partyId, BusinessVerificationRequest request) {
        // TODO: Implement Onfido business verification
        return Mono.error(new UnsupportedOperationException("Onfido KYB provider not yet implemented"));
    }

    /** {@inheritDoc} */
    @Override
    public Mono<CorporateDocumentCheckResult> checkCorporateDocument(UUID documentId, CorporateDocumentCheckRequest request) {
        // TODO: Implement Onfido corporate document check
        return Mono.error(new UnsupportedOperationException("Onfido KYB provider not yet implemented"));
    }

    /** {@inheritDoc} */
    @Override
    public Mono<UboVerificationResult> verifyUbo(UUID uboId, UboVerificationRequest request) {
        // TODO: Implement Onfido UBO verification
        return Mono.error(new UnsupportedOperationException("Onfido KYB provider not yet implemented"));
    }

    /** {@inheritDoc} */
    @Override
    public Mono<VerificationStatusResult> getVerificationStatus(String externalReferenceId) {
        // TODO: Implement Onfido verification status check
        return Mono.error(new UnsupportedOperationException("Onfido KYB provider not yet implemented"));
    }
}
