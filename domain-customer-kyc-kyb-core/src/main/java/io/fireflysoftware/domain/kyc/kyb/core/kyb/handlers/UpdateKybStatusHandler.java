package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.VerifyKybCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles approving a KYB verification by updating its status to VERIFIED
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class UpdateKybStatusHandler extends CommandHandler<VerifyKybCommand, Void> {

    private final KybVerificationApi kybVerificationApi;

    /**
     * Updates the KYB verification status to VERIFIED.
     *
     * @param cmd the verify command containing caseId and partyId
     * @return an empty Mono on success
     */
    @Override
    protected Mono<Void> doHandle(VerifyKybCommand cmd) {
        KybVerificationDTO updateDto = new KybVerificationDTO()
                .verificationStatus(STATUS_VERIFIED)
                .verificationDate(LocalDateTime.now());

        return kybVerificationApi.updateKybVerification(
                        cmd.getPartyId(), cmd.getCaseId(), updateDto, UUID.randomUUID().toString())
                .then();
    }
}
