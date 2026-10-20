package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.FailKybCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles rejecting a KYB verification by updating its status to FAILED
 * with notes via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class FailKybHandler extends CommandHandler<FailKybCommand, Void> {

    private final KybVerificationApi kybVerificationApi;

    /**
     * Updates the KYB verification status to FAILED with the given rejection reason.
     *
     * @param cmd the fail command containing caseId, partyId, and reason
     * @return an empty Mono on success
     */
    @Override
    protected Mono<Void> doHandle(FailKybCommand cmd) {
        KybVerificationDTO updateDto = new KybVerificationDTO()
                .verificationStatus(STATUS_FAILED)
                .verificationNotes(cmd.getReason())
                .verificationDate(LocalDateTime.now());

        return kybVerificationApi.updateKybVerification(
                        cmd.getPartyId(), cmd.getCaseId(), updateDto, UUID.randomUUID().toString())
                .then();
    }
}
