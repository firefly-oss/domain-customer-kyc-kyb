package io.fireflysoftware.domain.kyc.kyb.core.kyc.services;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface KycService {
    
    /**
     * Opens a new KYC case with required due diligence level.
     * @return Mono containing the result of opening the KYC case
     */
    Mono<SagaResult> openCase();
    
    /**
     * Attaches identity/POA/selfie documents (fingerprint ref) to a KYC case.
     * @param caseId the unique identifier of the KYC case
     * @param command the command containing document information to attach
     * @return Mono containing the result of attaching evidence
     */
    Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command);
    
    /**
     * Marks the dossier as complete for review.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of marking documents complete
     */
    Mono<SagaResult> markDocsComplete(UUID caseId);
    
    /**
     * Records PEP/adverse media status affecting the decision.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of recording PEP/adverse media status
     */
    Mono<SagaResult> recordPepAdverseMedia(UUID caseId);
    
    /**
     * Approves KYC at specified level and sets expiration.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of KYC verification
     */
    Mono<SagaResult> verify(UUID caseId);
    
    /**
     * Closes the KYC case as rejected due to fraud/inconsistency.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of failing the KYC case
     */
    Mono<SagaResult> fail(UUID caseId);
    
    /**
     * Marks verification as expired by time.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of expiring the KYC case
     */
    Mono<SagaResult> expire(UUID caseId);
    
    /**
     * Renews verification with new level/expiry.
     * @param caseId the unique identifier of the KYC case
     * @return Mono containing the result of renewing the KYC case
     */
    Mono<SagaResult> renew(UUID caseId);
}
