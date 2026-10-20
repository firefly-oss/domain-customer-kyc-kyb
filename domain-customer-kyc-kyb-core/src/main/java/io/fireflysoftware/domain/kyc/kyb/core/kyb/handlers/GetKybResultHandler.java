package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybResultQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.QueryHandlerComponent;
import org.fireflyframework.cqrs.query.QueryHandler;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Retrieves a KYB verification result by party ID and verification ID.
 * Results are cached for 10 minutes — verification outcomes are immutable
 * once the workflow reaches the {@code evaluateResult} step.
 */
@Slf4j
@RequiredArgsConstructor
@QueryHandlerComponent(cacheable = true, cacheTtl = 600, metrics = true)
public class GetKybResultHandler extends QueryHandler<GetKybResultQuery, KybVerificationDTO> {

    private final KybVerificationApi kybVerificationApi;

    @Override
    protected Mono<KybVerificationDTO> doHandle(GetKybResultQuery query) {
        log.info("Fetching KYB verification result: partyId={}, verificationId={}",
                query.getPartyId(), query.getVerificationId());
        return kybVerificationApi.getKybVerification(query.getPartyId(), query.getVerificationId(), UUID.randomUUID().toString());
    }
}
