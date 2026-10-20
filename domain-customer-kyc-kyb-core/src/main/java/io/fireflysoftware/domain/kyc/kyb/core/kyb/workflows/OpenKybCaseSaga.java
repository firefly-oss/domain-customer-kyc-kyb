package io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.OpenKybCaseCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Saga that orchestrates opening a new KYB verification case.
 * Creates the verification record via the command bus and stores the
 * resulting verification ID in the execution context.
 */
@Saga(name = SAGA_OPEN_KYB_CASE)
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenKybCaseSaga {

    private final CommandBus commandBus;

    /**
     * Creates a KYB verification record and stores its identifier in the execution context.
     *
     * @param cmd the command to open a KYB case
     * @param ctx the saga execution context
     * @return a Mono emitting the newly created verification identifier
     */
    @SagaStep(id = STEP_CREATE_KYB_VERIFICATION, compensate = COMPENSATE_DELETE_KYB_VERIFICATION)
    @StepEvent(type = EVENT_KYB_CASE_OPENED)
    public Mono<UUID> createKybVerification(OpenKybCaseCommand cmd, ExecutionContext ctx) {
        return commandBus.<UUID>send(cmd)
                .doOnNext(verificationId -> ctx.putVariable(CTX_VERIFICATION_ID, verificationId));
    }

    /**
     * Compensation handler that logs a warning when a KYB verification creation cannot
     * be automatically reversed.
     *
     * @param verificationId the identifier of the verification that was created
     * @return an empty Mono
     */
    public Mono<Void> compensateDeleteKybVerification(UUID verificationId) {
        log.warn("KYB verification {} creation cannot be auto-reversed — logging compensation", verificationId);
        return Mono.empty();
    }
}
