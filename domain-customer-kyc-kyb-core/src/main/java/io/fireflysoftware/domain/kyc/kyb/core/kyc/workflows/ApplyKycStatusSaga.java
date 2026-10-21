package io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.ApplyKycStatusCommand;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Saga(name = ApplyKycStatusSaga.NAME)
@Service
public class ApplyKycStatusSaga {

    public static final String NAME = "ApplyKycStatusSaga";
    public static final String STEP = "applyStatus";

    private final CommandBus commandBus;

    public ApplyKycStatusSaga(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @SagaStep(id = STEP)
    @StepEvent(type = "kyc.status.applied")
    public Mono<Void> applyStatus(ApplyKycStatusCommand command) {
        return commandBus.send(command);
    }
}
