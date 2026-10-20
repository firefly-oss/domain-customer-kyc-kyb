package io.fireflysoftware.domain.kyc.kyb.core.kyc.services.impl;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.services.KycService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.fireflyframework.orchestration.saga.engine.SagaResult;

import java.util.UUID;

@Service
public class KycServiceImpl implements KycService {
    
    @Override
    public Mono<SagaResult> openCase() {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> markDocsComplete(UUID caseId) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> recordPepAdverseMedia(UUID caseId) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> verify(UUID caseId) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> fail(UUID caseId) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> expire(UUID caseId) {
        return Mono.empty();
    }
    
    @Override
    public Mono<SagaResult> renew(UUID caseId) {
        return Mono.empty();
    }
}
