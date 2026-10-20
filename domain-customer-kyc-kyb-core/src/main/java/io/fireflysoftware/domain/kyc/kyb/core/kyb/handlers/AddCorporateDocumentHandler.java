package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.CorporateDocumentsApi;
import io.fireflysoftware.core.kycb.sdk.model.CorporateDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AddCorporateDocumentCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that adds a corporate document to a KYB case via the core SDK.
 */
@CommandHandlerComponent
public class AddCorporateDocumentHandler extends CommandHandler<AddCorporateDocumentCommand, CorporateDocumentDTO> {

    private final CorporateDocumentsApi corporateDocumentsApi;

    public AddCorporateDocumentHandler(CorporateDocumentsApi corporateDocumentsApi) {
        this.corporateDocumentsApi = corporateDocumentsApi;
    }

    @Override
    protected Mono<CorporateDocumentDTO> doHandle(AddCorporateDocumentCommand cmd) {
        return corporateDocumentsApi.addCorporateDocument(cmd, UUID.randomUUID().toString());
    }
}
