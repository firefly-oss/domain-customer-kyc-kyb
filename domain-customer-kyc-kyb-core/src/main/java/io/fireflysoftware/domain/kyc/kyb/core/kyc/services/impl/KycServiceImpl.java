package io.fireflysoftware.domain.kyc.kyb.core.kyc.services.impl;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.ApplyKycStatusCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachKycEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.OpenKycCaseCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.VerifyKycCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.services.KycService;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows.ApplyKycStatusSaga;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.SAGA_ATTACH_KYC_EVIDENCE;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.SAGA_OPEN_KYC_CASE;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.SAGA_VERIFY_KYC;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.STEP_CREATE_KYC_VERIFICATION;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.STEP_RECORD_RISK_ASSESSMENT;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.STEP_UPDATE_VERIFICATION_STATUS;
import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.STEP_UPLOAD_DOCUMENT;

@Service
public class KycServiceImpl implements KycService {

    private final SagaEngine sagaEngine;

    public KycServiceImpl(SagaEngine sagaEngine) {
        this.sagaEngine = sagaEngine;
    }

    @Override
    public Mono<SagaResult> openCase(OpenKycCaseCommand command) {
        return sagaEngine.execute(SAGA_OPEN_KYC_CASE, StepInputs.builder()
                .forStepId(STEP_CREATE_KYC_VERIFICATION, command)
                .build());
    }

    @Override
    public Mono<SagaResult> attachEvidence(UUID caseId, AttachEvidenceCommand command) {
        AttachKycEvidenceCommand evidence = AttachKycEvidenceCommand.builder()
                .partyId(command.getPartyId())
                .caseId(caseId)
                .documentType(command.getDocumentType())
                .documentName(command.getDocumentName())
                .documentContent(command.getDocumentContent())
                .mimeType(command.getMimeType())
                .documentSize(command.getDocumentSize())
                .fingerprint(command.getFingerprint())
                .description(command.getDescription())
                .build();
        return sagaEngine.execute(SAGA_ATTACH_KYC_EVIDENCE, StepInputs.builder()
                .forStepId(STEP_UPLOAD_DOCUMENT, evidence)
                .build());
    }

    @Override
    public Mono<SagaResult> markDocsComplete(UUID caseId, UUID partyId) {
        return applyStatus(caseId, partyId, "DOCS_COMPLETE", "Documents complete");
    }

    @Override
    public Mono<SagaResult> recordPepAdverseMedia(UUID caseId, UUID partyId) {
        return applyStatus(caseId, partyId, "PEP_RECORDED", "PEP or adverse media recorded");
    }

    @Override
    public Mono<SagaResult> verify(UUID caseId, UUID partyId) {
        VerifyKycCommand command = VerifyKycCommand.builder().caseId(caseId).partyId(partyId).build();
        return sagaEngine.execute(SAGA_VERIFY_KYC, StepInputs.builder()
                .forStepId(STEP_UPDATE_VERIFICATION_STATUS, command)
                .forStepId(STEP_RECORD_RISK_ASSESSMENT, command)
                .build());
    }

    @Override
    public Mono<SagaResult> fail(UUID caseId, UUID partyId) {
        return applyStatus(caseId, partyId, "FAILED", "KYC rejected");
    }

    @Override
    public Mono<SagaResult> expire(UUID caseId, UUID partyId) {
        return applyStatus(caseId, partyId, "EXPIRED", "KYC expired");
    }

    @Override
    public Mono<SagaResult> renew(UUID caseId, UUID partyId) {
        return applyStatus(caseId, partyId, "RENEWED", "KYC renewed");
    }

    private Mono<SagaResult> applyStatus(UUID caseId, UUID partyId, String status, String reason) {
        return sagaEngine.execute(ApplyKycStatusSaga.NAME, StepInputs.builder()
                .forStepId(ApplyKycStatusSaga.STEP, new ApplyKycStatusCommand(partyId, caseId, status, reason))
                .build());
    }
}
