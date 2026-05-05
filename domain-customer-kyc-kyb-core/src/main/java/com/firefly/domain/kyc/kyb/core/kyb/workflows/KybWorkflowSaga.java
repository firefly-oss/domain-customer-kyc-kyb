package com.firefly.domain.kyc.kyb.core.kyb.workflows;

import com.firefly.core.kycb.sdk.api.ComplianceCasesApi;
import com.firefly.core.kycb.sdk.api.CorporateDocumentsApi;
import com.firefly.core.kycb.sdk.api.KybVerificationApi;
import com.firefly.core.kycb.sdk.api.UboManagementApi;
import com.firefly.core.kycb.sdk.model.KybVerificationDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.DocumentData;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RegisterUbosCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RequestKybVerificationCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmitCorporateDocumentsCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.UboData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.firefly.domain.kyc.kyb.core.kyb.workflows.KybWorkflowConstants.*;

/**
 * Orchestrates the end-to-end KYB (Know Your Business) verification workflow.
 *
 * <h3>Topology (DAG)</h3>
 * <pre>
 *   [createKybCase]
 *        ├── [submitCorporateDocuments]  ─┐
 *        └── [registerUbos]              ─┴── [requestVerification] ── [evaluateResult]
 * </pre>
 *
 * <p>Steps 2 and 3 run in parallel within the same topology layer. Step 4 is
 * the join point that waits for both. Step 5 is terminal with no compensation.</p>
 *
 * <h3>Compensation order (STRICT_SEQUENTIAL, reverse)</h3>
 * <pre>
 *   cancelVerification → removeUbos / deleteDocuments → cancelCase
 * </pre>
 *
 * <h3>Context key contract</h3>
 * <ul>
 *   <li>Step 1 writes: {@code caseId}, {@code partyId}, {@code documents}, {@code ubos}</li>
 *   <li>Step 2 writes: {@code documentIds}</li>
 *   <li>Step 3 writes: {@code uboIds}</li>
 *   <li>Step 4 writes: {@code verificationId}, {@code verificationStatus}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Saga(name = SAGA_NAME, layerConcurrency = 0)
public class KybWorkflowSaga {

    private final CommandBus commandBus;
    private final ComplianceCasesApi complianceCasesApi;
    private final CorporateDocumentsApi corporateDocumentsApi;
    private final UboManagementApi uboManagementApi;
    private final KybVerificationApi kybVerificationApi;

    // ─────────────────────────────────────────────────────────────────────────
    // Step 1 — Create compliance case
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Opens a KYB compliance case and stores all saga-wide data in context.
     * This is the root step: no {@code dependsOn}, so the injected {@code input}
     * parameter is reliably resolved by the ArgumentResolver.
     */
    @SagaStep(
            id = STEP_CREATE_CASE,
            compensate = COMPENSATE_CANCEL_CASE,
            retry = 2,
            backoffMs = 1000,
            timeoutMs = 30000
    )
    @StepEvent(type = EVENT_CASE_CREATED)
    public Mono<UUID> createKybCase(KybWorkflowInput input, ExecutionContext ctx) {
        CreateKybCaseCommand cmd = CreateKybCaseCommand.builder()
                .partyId(input.partyId())
                .businessName(input.businessName())
                .registrationNumber(input.registrationNumber())
                .tenantId(input.tenantId())
                .build();

        return commandBus.<UUID>send(cmd)
                .doOnNext(caseId -> {
                    ctx.putVariable(CTX_CASE_ID, caseId);
                    ctx.putVariable(CTX_PARTY_ID, input.partyId());
                    ctx.putVariable(CTX_DOCUMENTS, input.documents());
                    ctx.putVariable(CTX_UBOS, input.ubos());
                    log.info("KYB saga step createKybCase: caseId={}, partyId={}", caseId, input.partyId());
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 2 — Submit corporate documents (parallel with step 3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Uploads corporate documents. Reads all data from {@code ExecutionContext}
     * to avoid relying on the injected cmd parameter in a downstream step.
     */
    @SagaStep(
            id = STEP_SUBMIT_DOCUMENTS,
            dependsOn = STEP_CREATE_CASE,
            compensate = COMPENSATE_DELETE_DOCUMENTS,
            retry = 2,
            backoffMs = 1000,
            timeoutMs = 60000
    )
    @StepEvent(type = EVENT_DOCUMENTS_SUBMITTED)
    public Mono<SubmissionResult> submitCorporateDocuments(ExecutionContext ctx) {
        UUID caseId = (UUID) ctx.getVariable(CTX_CASE_ID);
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        @SuppressWarnings("unchecked")
        List<DocumentData> documents = (List<DocumentData>) ctx.getVariable(CTX_DOCUMENTS);

        log.info("KYB saga step submitCorporateDocuments: caseId={}", caseId);

        SubmitCorporateDocumentsCommand cmd = SubmitCorporateDocumentsCommand.builder()
                .caseId(caseId)
                .partyId(partyId)
                .documents(documents)
                .build();

        return commandBus.<SubmissionResult>send(cmd)
                .doOnNext(result -> ctx.putVariable(CTX_DOCUMENT_IDS, result.ids()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 3 — Register UBOs (parallel with step 2)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Registers Ultimate Beneficial Owners. Reads all data from {@code ExecutionContext}.
     */
    @SagaStep(
            id = STEP_REGISTER_UBOS,
            dependsOn = STEP_CREATE_CASE,
            compensate = COMPENSATE_REMOVE_UBOS,
            retry = 2,
            backoffMs = 1000,
            timeoutMs = 60000
    )
    @StepEvent(type = EVENT_UBOS_REGISTERED)
    public Mono<SubmissionResult> registerUbos(ExecutionContext ctx) {
        UUID caseId = (UUID) ctx.getVariable(CTX_CASE_ID);
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        @SuppressWarnings("unchecked")
        List<UboData> ubos = (List<UboData>) ctx.getVariable(CTX_UBOS);

        log.info("KYB saga step registerUbos: caseId={}", caseId);

        RegisterUbosCommand cmd = RegisterUbosCommand.builder()
                .caseId(caseId)
                .partyId(partyId)
                .ubos(ubos)
                .build();

        return commandBus.<SubmissionResult>send(cmd)
                .doOnNext(result -> ctx.putVariable(CTX_UBO_IDS, result.ids()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 4 — Request KYB verification (join point: waits for steps 2 AND 3)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Triggers external verification via {@code KybVerificationPort} and persists
     * the result. Stores verificationId and status in context for the evaluateResult step.
     */
    @SagaStep(
            id = STEP_REQUEST_VERIFICATION,
            dependsOn = {STEP_SUBMIT_DOCUMENTS, STEP_REGISTER_UBOS},
            compensate = COMPENSATE_CANCEL_VERIFICATION,
            retry = 1,
            timeoutMs = 90000
    )
    @StepEvent(type = EVENT_VERIFICATION_REQUESTED)
    public Mono<UUID> requestVerification(ExecutionContext ctx) {
        UUID caseId = (UUID) ctx.getVariable(CTX_CASE_ID);
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);

        log.info("KYB saga step requestVerification: caseId={}", caseId);

        RequestKybVerificationCommand cmd = RequestKybVerificationCommand.builder()
                .caseId(caseId)
                .partyId(partyId)
                .build();

        return commandBus.<KybVerificationDTO>send(cmd)
                .doOnNext(dto -> {
                    ctx.putVariable(CTX_VERIFICATION_ID, dto.getKybVerificationId());
                    ctx.putVariable(CTX_VERIFICATION_STATUS, dto.getVerificationStatus());
                })
                .map(KybVerificationDTO::getKybVerificationId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 5 — Evaluate result (terminal, no compensation)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Updates the compliance case status to the final verification outcome and
     * emits the cross-domain {@value KybWorkflowConstants#EVENT_VERIFICATION_COMPLETED}
     * event for downstream consumers (e.g., exp-onboarding, core-lending).
     *
     * <p>Returns {@code Mono.just(status)} — never {@code Mono.empty()} — because
     * the saga engine treats an empty Mono as a step failure.</p>
     */
    @SagaStep(
            id = STEP_EVALUATE_RESULT,
            dependsOn = STEP_REQUEST_VERIFICATION,
            compensate = COMPENSATE_EVALUATE_RESULT,
            timeoutMs = 30000
    )
    @StepEvent(type = EVENT_VERIFICATION_COMPLETED)
    public Mono<String> evaluateResult(ExecutionContext ctx) {
        UUID caseId = (UUID) ctx.getVariable(CTX_CASE_ID);
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        UUID verificationId = (UUID) ctx.getVariable(CTX_VERIFICATION_ID);
        String verificationStatus = (String) ctx.getVariable(CTX_VERIFICATION_STATUS);

        log.info("KYB saga step evaluateResult: caseId={}, status={}", caseId, verificationStatus);

        String caseStatus = VERIFICATION_STATUS_VERIFIED.equals(verificationStatus)
                ? CASE_STATUS_VERIFIED
                : CASE_STATUS_REJECTED;

        return complianceCasesApi.getComplianceCase(caseId, UUID.randomUUID().toString())
                .flatMap(existing -> {
                    existing.caseStatus(caseStatus);
                    return complianceCasesApi.updateComplianceCase(
                            caseId, existing, UUID.randomUUID().toString());
                })
                .doOnSuccess(updated -> log.info(
                        "KYB workflow complete: caseId={}, partyId={}, verificationId={}, finalStatus={}",
                        caseId, partyId, verificationId, caseStatus))
                .thenReturn(caseStatus);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Compensation methods
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Deletes the compliance case. Called when any step after {@code createKybCase} fails.
     */
    public Mono<Void> cancelCase(UUID caseId, ExecutionContext ctx) {
        UUID id = (UUID) ctx.getVariable(CTX_CASE_ID);
        if (id == null) return Mono.empty();
        log.info("Compensating createKybCase: deleting caseId={}", id);
        return complianceCasesApi.deleteComplianceCase(id, UUID.randomUUID().toString());
    }

    /**
     * Deletes all uploaded corporate documents. Called when a step after
     * {@code submitCorporateDocuments} fails.
     */
    @SuppressWarnings("unchecked")
    public Mono<Void> deleteDocuments(SubmissionResult result, ExecutionContext ctx) {
        List<UUID> ids = (List<UUID>) ctx.getVariable(CTX_DOCUMENT_IDS);
        if (ids == null || ids.isEmpty()) return Mono.empty();
        log.info("Compensating submitCorporateDocuments: deleting {} document(s)", ids.size());
        return Flux.fromIterable(ids)
                .flatMap(docId -> corporateDocumentsApi.deleteCorporateDocument(docId, UUID.randomUUID().toString()))
                .then();
    }

    /**
     * Deletes all registered UBOs. Called when a step after {@code registerUbos} fails.
     */
    @SuppressWarnings("unchecked")
    public Mono<Void> removeUbos(SubmissionResult result, ExecutionContext ctx) {
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        List<UUID> ids = (List<UUID>) ctx.getVariable(CTX_UBO_IDS);
        if (ids == null || ids.isEmpty() || partyId == null) return Mono.empty();
        log.info("Compensating registerUbos: removing {} UBO(s) for partyId={}", ids.size(), partyId);
        return Flux.fromIterable(ids)
                .flatMap(uboId -> uboManagementApi.deleteUbo(partyId, uboId, UUID.randomUUID().toString()))
                .then();
    }

    /**
     * Deletes the KYB verification record. Called when {@code evaluateResult} fails.
     */
    public Mono<Void> cancelVerification(UUID verificationId, ExecutionContext ctx) {
        UUID partyId = (UUID) ctx.getVariable(CTX_PARTY_ID);
        UUID vId = (UUID) ctx.getVariable(CTX_VERIFICATION_ID);
        if (vId == null || partyId == null) return Mono.empty();
        log.info("Compensating requestVerification: deleting verificationId={}", vId);
        return kybVerificationApi.deleteKybVerification(partyId, vId, UUID.randomUUID().toString());
    }

    /**
     * No-op compensation for the terminal {@code evaluateResult} step.
     *
     * <p>The case-status update done by {@code evaluateResult} is the saga's final
     * action; there is nothing meaningful to roll back. This method exists solely
     * so that {@code @SagaStep.compensate} is non-empty and the orchestration
     * validator does not emit a benign "no compensation defined" warning at
     * startup. Earlier steps still have their own compensations triggered by the
     * STRICT_SEQUENTIAL policy if a downstream failure ever occurs.</p>
     */
    public Mono<Void> compensateEvaluateResult(String caseStatus, ExecutionContext ctx) {
        log.debug("Compensation no-op for evaluateResult: terminal step, caseStatus={}", caseStatus);
        return Mono.empty();
    }
}
