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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybCaseQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Retrieves a KYB compliance case by its unique identifier.
 * Results are cached for 5 minutes to reduce load on the core service.
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent(cacheable = true, cacheTtl = 300, metrics = true)
public class GetKybCaseHandler extends QueryHandler<GetKybCaseQuery, ComplianceCaseDTO> {

    private final ComplianceCasesApi complianceCasesApi;

    @Override
    protected Mono<ComplianceCaseDTO> doHandle(GetKybCaseQuery query) {
        log.info("Fetching KYB case: caseId={}", query.getCaseId());
        return complianceCasesApi.getComplianceCase(query.getCaseId(), UUID.randomUUID().toString());
    }
}
