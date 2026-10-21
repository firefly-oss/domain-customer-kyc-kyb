package io.fireflysoftware.domain.kyc.kyb.core.kyc.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KycVerificationDocumentsApi;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.DeleteKycEvidenceCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

@CommandHandlerComponent
public class DeleteKycEvidenceHandler extends CommandHandler<DeleteKycEvidenceCommand, Void> {

    private final KycVerificationDocumentsApi kycVerificationDocumentsApi;

    public DeleteKycEvidenceHandler(KycVerificationDocumentsApi kycVerificationDocumentsApi) {
        this.kycVerificationDocumentsApi = kycVerificationDocumentsApi;
    }

    @Override
    protected Mono<Void> doHandle(DeleteKycEvidenceCommand cmd) {
        return kycVerificationDocumentsApi.deleteVerificationDocument(
                cmd.partyId(), cmd.verificationId(), cmd.documentId(), UUID.randomUUID().toString());
    }
}
