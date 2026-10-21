package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KycVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.ApplyKycStatusCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@CommandHandlerComponent
public class ApplyKycStatusHandler extends CommandHandler<ApplyKycStatusCommand, Void> {

    private final KycVerificationApi kycVerificationApi;

    public ApplyKycStatusHandler(KycVerificationApi kycVerificationApi) {
        this.kycVerificationApi = kycVerificationApi;
    }

    @Override
    protected Mono<Void> doHandle(ApplyKycStatusCommand cmd) {
        KycVerificationDTO update = new KycVerificationDTO()
                .verificationStatus(cmd.getStatus())
                .verificationDate(LocalDateTime.now());
        if (cmd.getReason() != null && !cmd.getReason().isBlank()) {
            update.rejectionReason(cmd.getReason());
        }
        return kycVerificationApi.updateKycVerification(
                cmd.getPartyId(), cmd.getCaseId(), update, UUID.randomUUID().toString()).then();
    }
}
