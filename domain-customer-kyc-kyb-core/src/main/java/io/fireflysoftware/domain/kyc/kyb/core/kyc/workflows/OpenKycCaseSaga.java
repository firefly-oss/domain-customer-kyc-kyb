package io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.DeleteKycVerificationCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.OpenKycCaseCommand;
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
 * Saga that orchestrates opening a new KYC verification case.
 * Creates the verification record via the command bus and stores the
 * resulting verification ID in the execution context.
 */
@Saga(name = SAGA_OPEN_KYC_CASE)
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenKycCaseSaga {

    private final CommandBus commandBus;

    /**
     * Creates a KYC verification record and stores its identifier in the execution context.
     *
     * @param cmd the command to open a KYC case
     * @param ctx the saga execution context
     * @return a Mono emitting the newly created verification identifier
     */
    @SagaStep(id = STEP_CREATE_KYC_VERIFICATION, compensate = COMPENSATE_DELETE_VERIFICATION)
    @StepEvent(type = EVENT_KYC_CASE_OPENED)
    public Mono<UUID> createKycVerification(OpenKycCaseCommand cmd, ExecutionContext ctx) {
        return commandBus.<UUID>send(cmd)
                .doOnNext(verificationId -> {
                    ctx.putVariable(CTX_VERIFICATION_ID, verificationId);
                    ctx.putVariable("partyId", cmd.getPartyId());
                });
    }

    /**
     * Deletes the verification the step just created.
     */
    public Mono<Void> compensateDeleteVerification(UUID verificationId, ExecutionContext ctx) {
        UUID partyId = ctx.getVariableAs("partyId", UUID.class);
        if (verificationId == null || partyId == null) {
            return Mono.empty();
        }
        return commandBus.send(new DeleteKycVerificationCommand(partyId, verificationId));
    }
}
