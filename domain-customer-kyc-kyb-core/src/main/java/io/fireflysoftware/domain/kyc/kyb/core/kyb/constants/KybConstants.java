package io.fireflysoftware.domain.kyc.kyb.core.kyb.constants;

/**
 * Constants for KYB saga orchestration.
 */
public final class KybConstants {

    private KybConstants() {
    }

    // Saga names

    public static final String SAGA_START_KYB = "StartKybSaga";
    public static final String SAGA_ATTACH_EVIDENCE = "AttachKybEvidenceSaga";
    public static final String SAGA_VERIFY_KYB = "VerifyKybSaga";

    // Step IDs

    public static final String STEP_CREATE_CASE = "createCase";
    public static final String STEP_ATTACH_DOCUMENT = "attachDocument";
    public static final String STEP_MARK_DOCS_COMPLETE = "markDocsComplete";
    public static final String STEP_REQUEST_VERIFICATION = "requestVerification";
    public static final String STEP_EVALUATE_RESULT = "evaluateResult";

    // Events

    public static final String EVENT_KYB_CASE_CREATED = "kyb.case.created";
    public static final String EVENT_KYB_EVIDENCE_ATTACHED = "kyb.evidence.attached";
    public static final String EVENT_KYB_DOCS_COMPLETE = "kyb.docs.complete";
    public static final String EVENT_KYB_VERIFICATION_REQUESTED = "kyb.verification.requested";
    public static final String EVENT_KYB_VERIFICATION_COMPLETED = "kyb.verification.completed";

    // Context variables

    public static final String CTX_CASE_ID = "caseId";
    public static final String CTX_VERIFICATION_STATUS = "verificationStatus";
}
