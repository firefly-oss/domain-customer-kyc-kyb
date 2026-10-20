package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RecordKybVerificationCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that records a KYB verification result in the core layer via the SDK.
 */
@CommandHandlerComponent
public class RecordKybVerificationHandler extends CommandHandler<RecordKybVerificationCommand, KybVerificationDTO> {

    private final KybVerificationApi kybVerificationApi;

    public RecordKybVerificationHandler(KybVerificationApi kybVerificationApi) {
        this.kybVerificationApi = kybVerificationApi;
    }

    @Override
    protected Mono<KybVerificationDTO> doHandle(RecordKybVerificationCommand cmd) {
        var verificationDto = new KybVerificationDTO()
                .verificationStatus(cmd.getVerificationStatus());
        return kybVerificationApi.createKybVerification(cmd.getCaseId(), verificationDto, UUID.randomUUID().toString());
    }
}
