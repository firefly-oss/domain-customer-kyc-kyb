/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
