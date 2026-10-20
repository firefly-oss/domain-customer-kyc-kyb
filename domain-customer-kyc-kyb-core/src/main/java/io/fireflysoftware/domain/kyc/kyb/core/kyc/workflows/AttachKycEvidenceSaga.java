package io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachKycEvidenceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Saga that orchestrates attaching evidence documents to an existing KYC case.
 * Uploads the document via the command bus and stores the resulting document ID
 * in the execution context.
 */
@Saga(name = SAGA_ATTACH_KYC_EVIDENCE)
@Service
@Slf4j
@RequiredArgsConstructor
public class AttachKycEvidenceSaga {

    private final CommandBus commandBus;

    /**
     * Uploads a document for a KYC case and stores its identifier in the execution context.
     *
     * @param cmd the command containing document metadata
     * @param ctx the saga execution context
     * @return a Mono emitting the newly created document identifier
     */
    @SagaStep(id = STEP_UPLOAD_DOCUMENT, compensate = COMPENSATE_DELETE_DOCUMENT)
    @StepEvent(type = EVENT_KYC_EVIDENCE_ATTACHED)
    public Mono<UUID> uploadDocument(AttachKycEvidenceCommand cmd, ExecutionContext ctx) {
        return commandBus.<UUID>send(cmd)
                .doOnNext(documentId -> ctx.putVariable(CTX_DOCUMENT_ID, documentId));
    }

    /**
     * Compensation handler that logs a warning when a KYC document upload cannot
     * be automatically reversed.
     *
     * @param documentId the identifier of the document that was uploaded
     * @return an empty Mono
     */
    public Mono<Void> compensateDeleteDocument(UUID documentId) {
        log.warn("KYC document {} upload cannot be auto-reversed — logging compensation", documentId);
        return Mono.empty();
    }
}
