package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.DeleteKycVerificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class DeleteKycVerificationHandler extends CommandHandler<DeleteKycVerificationCommand, Void> {

    private final KycVerificationApi kycVerificationApi;

    public DeleteKycVerificationHandler(KycVerificationApi kycVerificationApi) {
        this.kycVerificationApi = kycVerificationApi;
    }

    @Override
    protected Mono<Void> doHandle(DeleteKycVerificationCommand cmd) {
        return kycVerificationApi.deleteKycVerification(cmd.partyId(), cmd.verificationId(), UUID.randomUUID().toString());
    }
}
