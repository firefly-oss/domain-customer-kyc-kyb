package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.OpenKybCaseCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles opening a new KYB verification case by creating a verification record
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class CreateKybVerificationHandler extends CommandHandler<OpenKybCaseCommand, UUID> {

    private final KybVerificationApi kybVerificationApi;

    /**
     * Creates a new KYB verification record in the kycb-mgmt service.
     *
     * @param cmd the open-case command containing partyId
     * @return a Mono emitting the newly created verification identifier
     */
    @Override
    protected Mono<UUID> doHandle(OpenKybCaseCommand cmd) {
        KybVerificationDTO dto = new KybVerificationDTO()
                .partyId(cmd.getPartyId())
                .verificationStatus(STATUS_PENDING)
                .verificationDate(LocalDateTime.now());

        return kybVerificationApi.createKybVerification(
                        cmd.getPartyId(), dto, UUID.randomUUID().toString())
                .map(KybVerificationDTO::getKybVerificationId);
    }
}
