package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DeleteKybVerificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class DeleteKybVerificationHandler extends CommandHandler<DeleteKybVerificationCommand, Void> {

    private final KybVerificationApi kybVerificationApi;

    public DeleteKybVerificationHandler(KybVerificationApi kybVerificationApi) {
        this.kybVerificationApi = kybVerificationApi;
    }

    @Override
    protected Mono<Void> doHandle(DeleteKybVerificationCommand cmd) {
        return kybVerificationApi.deleteKybVerification(cmd.partyId(), cmd.verificationId(), UUID.randomUUID().toString());
    }
}
