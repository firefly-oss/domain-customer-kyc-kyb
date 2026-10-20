package io.fireflysoftware.domain.kyc.kyb.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

/**
 * Stub implementation of {@link KybVerificationPort} for local development and testing.
 * Always returns {@code VERIFIED} after a simulated 200 ms network delay.
 *
 * <p>Active by default unless {@code integration.kyb-verification.provider} is set to a
 * real provider value (e.g., {@code sumsub}, {@code onfido}, {@code dow-jones}).</p>
 *
 * <p>TODO: Replace stub with real KYB provider (e.g., Sumsub, Onfido, Dow Jones)</p>
 */
@Slf4j
@Component
@ConditionalOnProperty(
        name = "integration.kyb-verification.provider",
        havingValue = "stub",
        matchIfMissing = true
)
public class StubKybVerificationAdapter implements KybVerificationPort {

    @Override
    public Mono<KybVerificationResult> verify(UUID caseId) {
        log.info("StubKybVerificationAdapter: simulating KYB verification for caseId={}", caseId);
        return Mono.delay(Duration.ofMillis(200))
                .map(tick -> KybVerificationResult.verified())
                .doOnSuccess(result -> log.info(
                        "StubKybVerificationAdapter: KYB verification complete for caseId={}, status={}",
                        caseId, result.getStatus()));
    }
}
