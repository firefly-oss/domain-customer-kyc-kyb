package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DeleteKybCaseCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Handler that deletes (cancels) a KYB compliance case via the core SDK.
 */
@CommandHandlerComponent
public class DeleteKybCaseHandler extends CommandHandler<DeleteKybCaseCommand, Void> {

    private final ComplianceCasesApi complianceCasesApi;

    public DeleteKybCaseHandler(ComplianceCasesApi complianceCasesApi) {
        this.complianceCasesApi = complianceCasesApi;
    }

    @Override
    protected Mono<Void> doHandle(DeleteKybCaseCommand cmd) {
        return complianceCasesApi.deleteComplianceCase(cmd.getCaseId(), UUID.randomUUID().toString()).then();
    }
}
