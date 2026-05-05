package com.firefly.domain.kyc.kyb.core.kyb.workflows;

/**
 * Central constants for the KYB workflow saga — step IDs, compensation names,
 * context keys, and domain event types. Using constants avoids magic strings
 * scattered across saga steps and enables consistent renaming.
 */
public final class KybWorkflowConstants {

    private KybWorkflowConstants() {}

    // ── Saga identity ────────────────────────────────────────────────────────
    public static final String SAGA_NAME = "kyb-workflow";

    // ── Step IDs ─────────────────────────────────────────────────────────────
    public static final String STEP_CREATE_CASE           = "createKybCase";
    public static final String STEP_SUBMIT_DOCUMENTS      = "submitCorporateDocuments";
    public static final String STEP_REGISTER_UBOS         = "registerUbos";
    public static final String STEP_REQUEST_VERIFICATION  = "requestVerification";
    public static final String STEP_EVALUATE_RESULT       = "evaluateResult";

    // ── Compensation method names ─────────────────────────────────────────────
    public static final String COMPENSATE_CANCEL_CASE          = "cancelCase";
    public static final String COMPENSATE_DELETE_DOCUMENTS     = "deleteDocuments";
    public static final String COMPENSATE_REMOVE_UBOS          = "removeUbos";
    public static final String COMPENSATE_CANCEL_VERIFICATION  = "cancelVerification";
    public static final String COMPENSATE_EVALUATE_RESULT      = "compensateEvaluateResult";

    // ── SagaContext variable keys ─────────────────────────────────────────────
    public static final String CTX_CASE_ID           = "caseId";
    public static final String CTX_PARTY_ID          = "partyId";
    public static final String CTX_DOCUMENTS         = "documents";
    public static final String CTX_UBOS              = "ubos";
    public static final String CTX_DOCUMENT_IDS      = "documentIds";
    public static final String CTX_UBO_IDS           = "uboIds";
    public static final String CTX_VERIFICATION_ID   = "verificationId";
    public static final String CTX_VERIFICATION_STATUS = "verificationStatus";

    // ── Domain events (published to Kafka via @StepEvent) ────────────────────
    public static final String EVENT_CASE_CREATED            = "kyb.case.created";
    public static final String EVENT_DOCUMENTS_SUBMITTED     = "kyb.documents.submitted";
    public static final String EVENT_UBOS_REGISTERED         = "kyb.ubos.registered";
    public static final String EVENT_VERIFICATION_REQUESTED  = "kyb.verification.requested";
    public static final String EVENT_VERIFICATION_COMPLETED  = "kyb.verification.completed";

    // ── ComplianceCase status values ─────────────────────────────────────────
    public static final String CASE_STATUS_OPEN        = "OPEN";
    public static final String CASE_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String CASE_STATUS_VERIFIED    = "VERIFIED";
    public static final String CASE_STATUS_REJECTED    = "REJECTED";

    // ── KybVerification status values ─────────────────────────────────────────
    public static final String VERIFICATION_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String VERIFICATION_STATUS_VERIFIED    = "VERIFIED";
    public static final String VERIFICATION_STATUS_REJECTED    = "REJECTED";

    // ── Case type ─────────────────────────────────────────────────────────────
    public static final String CASE_TYPE_KYB = "KYB";
}
