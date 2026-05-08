package com.firefly.domain.kyc.kyb.core.kyb.handlers;

import com.firefly.core.kycb.sdk.api.UboManagementApi;
import com.firefly.core.kycb.sdk.model.UboDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RegisterUbosCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import com.firefly.domain.kyc.kyb.core.kyb.commands.UboData;
import com.firefly.domain.kyc.kyb.core.kyb.mappers.KybMapper;
import com.firefly.domain.kyc.kyb.core.util.IdempotencyKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * Registers each Ultimate Beneficial Owner via {@code UboManagementApi}.
 * UBOs are processed concurrently (Flux.flatMap with unbounded concurrency).
 * Returns a {@link SubmissionResult} containing all created UBO IDs.
 */
@Slf4j
@RequiredArgsConstructor
@CommandHandlerComponent(timeout = 60000, retries = 2, metrics = true, tracing = true)
public class RegisterUbosHandler extends CommandHandler<RegisterUbosCommand, SubmissionResult> {

    private final UboManagementApi uboManagementApi;
    private final KybMapper kybMapper;

    @Override
    protected Mono<SubmissionResult> doHandle(RegisterUbosCommand cmd) {
        log.info("Registering {} UBO(s) for caseId={}", cmd.getUbos().size(), cmd.getCaseId());

        return Flux.fromIterable(cmd.getUbos())
                .flatMap(ubo -> registerUbo(ubo, cmd.getPartyId()))
                .collectList()
                .map(SubmissionResult::new)
                .doOnSuccess(result ->
                        log.info("Registered {} UBO(s) for caseId={}", result.ids().size(), cmd.getCaseId()));
    }

    private Mono<UUID> registerUbo(UboData ubo, UUID partyId) {
        UboDTO dto = kybMapper.toDto(ubo);
        dto.partyId(partyId);

        // Deterministic key: same partyId + same naturalPersonId must produce
        // the same key on every retry (handler retry policy is retries=2).
        // Without this, a transient failure followed by a successful retry
        // would create a duplicate UBO row in core.
        String idempotencyKey = IdempotencyKeys.of(
                "register-ubo", partyId.toString(),
                ubo.getNaturalPersonId() != null ? ubo.getNaturalPersonId().toString() : "null");

        return uboManagementApi
                .addUbo(partyId, dto, idempotencyKey)
                .map(result -> Objects.requireNonNull(result.getUboId(),
                        "Core service returned null uboId"));
    }
}
