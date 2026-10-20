package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KycVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.OpenKycCaseCommand;
import lombok.RequiredArgsConstructor;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Handles opening a new KYC verification case by creating a verification record
 * via the kycb-mgmt SDK.
 */
@CommandHandlerComponent
@RequiredArgsConstructor
public class CreateKycVerificationHandler extends CommandHandler<OpenKycCaseCommand, UUID> {

    private final KycVerificationApi kycVerificationApi;

    /**
     * Creates a new KYC verification record in the kycb-mgmt service.
     *
     * @param cmd the open-case command containing partyId and due-diligence level
     * @return a Mono emitting the newly created verification identifier
     */
    @Override
    protected Mono<UUID> doHandle(OpenKycCaseCommand cmd) {
        KycVerificationDTO dto = new KycVerificationDTO()
                .partyId(cmd.getPartyId())
                .verificationStatus(STATUS_PENDING)
                .verificationDate(LocalDateTime.now())
                .verificationMethod(cmd.getDueDiligenceLevel())
                .enhancedDueDiligence(LEVEL_ENHANCED.equals(cmd.getDueDiligenceLevel()));

        return kycVerificationApi.createKycVerification(
                        cmd.getPartyId(), dto, UUID.randomUUID().toString())
                .map(KycVerificationDTO::getKycVerificationId);
    }
}
