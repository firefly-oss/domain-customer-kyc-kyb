package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UpdateCaseStatusCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Handler that updates the status of a KYB compliance case via the core SDK.
 */
@CommandHandlerComponent
public class UpdateCaseStatusHandler extends CommandHandler<UpdateCaseStatusCommand, ComplianceCaseDTO> {

    private final ComplianceCasesApi complianceCasesApi;

    public UpdateCaseStatusHandler(ComplianceCasesApi complianceCasesApi) {
        this.complianceCasesApi = complianceCasesApi;
    }

    @Override
    protected Mono<ComplianceCaseDTO> doHandle(UpdateCaseStatusCommand cmd) {
        return complianceCasesApi.updateComplianceCase(
                cmd.getCaseId(),
                new ComplianceCaseDTO().caseStatus(cmd.getNewStatus()), UUID.randomUUID().toString());
    }
}
