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
