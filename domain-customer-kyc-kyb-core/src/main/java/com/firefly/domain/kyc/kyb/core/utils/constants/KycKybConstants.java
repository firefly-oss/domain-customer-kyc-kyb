package com.firefly.domain.kyc.kyb.core.utils.constants;

/**
 * Shared constants for the KYC/KYB domain service.
 */
public final class KycKybConstants {

    private KycKybConstants() {
        // utility class
    }

    // --- Statuses ---
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_FAILED = "FAILED";

    public static final String CASE_STATUS_OPEN = "OPEN";
    public static final String CASE_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String CASE_STATUS_VERIFIED = "VERIFIED";
    public static final String CASE_STATUS_REJECTED = "REJECTED";

    public static final String VERIFICATION_STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String VERIFICATION_STATUS_VERIFIED = "VERIFIED";
    public static final String VERIFICATION_STATUS_REJECTED = "REJECTED";

    // --- Shorthand status aliases ---
    public static final String OPEN = "OPEN";
    public static final String VERIFIED = "VERIFIED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String REJECTED = "REJECTED";

    // --- Types ---
    public static final String CASE_TYPE_KYB = "KYB";

    // --- Context keys ---
    public static final String CTX_CASE_ID = "caseId";
    public static final String CTX_DOCUMENT_ID = "documentId";
    public static final String CTX_DOCUMENT_IDS = "documentIds";
    public static final String CTX_VERIFICATION_ID = "verificationId";
    public static final String CTX_VERIFICATION_STATUS = "verificationStatus";

    // --- Saga names ---
    public static final String SAGA_OPEN_KYC_CASE = "open-kyc-case";
    public static final String SAGA_OPEN_KYB_CASE = "open-kyb-case";
    public static final String SAGA_ATTACH_KYC_EVIDENCE = "attach-kyc-evidence";
    public static final String SAGA_VERIFY_KYC = "verify-kyc";

    // --- Step names ---
    public static final String STEP_CREATE_KYC_VERIFICATION = "create-kyc-verification";
    public static final String STEP_CREATE_KYB_VERIFICATION = "create-kyb-verification";
    public static final String STEP_UPDATE_VERIFICATION_STATUS = "update-verification-status";
    public static final String STEP_RECORD_RISK_ASSESSMENT = "record-risk-assessment";
    public static final String STEP_UPLOAD_DOCUMENT = "upload-document";

    // --- Events ---
    public static final String EVENT_CASE_CREATED = "case-created";
    public static final String EVENT_KYC_CASE_OPENED = "kyc-case-opened";
    public static final String EVENT_KYC_EVIDENCE_ATTACHED = "kyc-evidence-attached";
    public static final String EVENT_KYC_VERIFIED = "kyc-verified";
    public static final String EVENT_VERIFICATION_REQUESTED = "verification-requested";
    public static final String EVENT_VERIFICATION_COMPLETED = "verification-completed";
    public static final String EVENT_KYB_CASE_CREATED = "kyb-case-created";
    public static final String EVENT_KYB_CASE_OPENED = "kyb-case-opened";
    public static final String EVENT_KYB_DOCS_COMPLETE = "kyb-docs-complete";
    public static final String EVENT_KYB_EVIDENCE_ATTACHED = "kyb-evidence-attached";
    public static final String EVENT_KYB_VERIFICATION_REQUESTED = "kyb-verification-requested";
    public static final String EVENT_KYB_VERIFICATION_COMPLETED = "kyb-verification-completed";

    // --- Compensation method names (must match the Java method names exactly;
    //     SagaRegistry resolves them via strict equality, no kebab-to-camel conversion) ---
    public static final String COMPENSATE_DELETE_VERIFICATION = "compensateDeleteVerification";
    public static final String COMPENSATE_DELETE_KYB_VERIFICATION = "compensateDeleteKybVerification";
    public static final String COMPENSATE_DELETE_DOCUMENT = "compensateDeleteDocument";

    // --- Due diligence ---
    public static final String LEVEL_ENHANCED = "ENHANCED";
    public static final double STUB_DOCUMENT_CONFIDENCE = 0.95;

    // --- Verification purposes ---
    public static final String PURPOSE_IDENTITY_VERIFICATION = "IDENTITY_VERIFICATION";
    public static final String PURPOSE_BUSINESS_VERIFICATION = "BUSINESS_VERIFICATION";

    // --- Risk assessment ---
    public static final String ASSESSMENT_TYPE_KYC = "KYC";
    public static final String RISK_LEVEL_LOW = "LOW";
    public static final String RISK_CATEGORY_STANDARD = "STANDARD";
}
