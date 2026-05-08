package com.firefly.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Value object carrying authorized-signer (Power of Attorney) data into the KYB workflow.
 *
 * <p>Passed inside {@link SubmitAuthorizedSignersCommand} and mapped to
 * {@code PowerOfAttorneyDTO} by {@code KybMapper} before the SDK call.</p>
 *
 * <p>The first four identity fields ({@code firstName}, {@code lastName},
 * {@code documentType}, {@code documentNumber}) describe the natural person
 * acting as attorney. They are not part of {@code PowerOfAttorneyDTO} itself
 * (which references the attorney by {@code attorneyId}); they are carried in
 * this value object so the BFF / experience layer can pass the UI shape through
 * a single endpoint, leaving downstream identity resolution to a separate step.</p>
 *
 * <p>{@code email}, {@code signingAuthorized}, {@code isPep} are propagated
 * directly to {@code PowerOfAttorneyDTO} (BE-5c).</p>
 */
@Data
@Builder
public class SignerData {

    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;

    /**
     * Resolved party ID of the natural person acting as attorney.
     * The caller (BFF) is responsible for registering the attorney via the
     * customer service and passing the resulting party UUID here. Required:
     * core service rejects {@code PowerOfAttorneyDTO} with a NOT NULL
     * constraint on {@code attorney_id}.
     */
    private UUID attorneyId;

    /**
     * UI role label (e.g. "CEO", "DIRECTOR"). Mapped to
     * {@code PowerOfAttorneyDTO.powerType} on the SDK call. The mapper
     * normalises the value to one of the supported enum keys
     * ({@code GENERAL}, {@code LIMITED}, {@code SPECIAL}, {@code TRADING});
     * unknown labels fall back to {@code GENERAL}.
     */
    private String role;

    /**
     * Reference to a corporate-document upload (UUID string) that represents
     * the power-of-attorney instrument. Mapped to
     * {@code PowerOfAttorneyDTO.corporateDocumentId} on the SDK call.
     */
    private String powerDocumentReference;

    /**
     * Optional contact email for the authorized signer.
     * Propagated to {@code PowerOfAttorneyDTO.email} on the core SDK call (BE-5c).
     */
    @Email(message = "email must be a valid email address")
    private String email;

    /**
     * Whether this signer is authorized to sign on behalf of the legal entity.
     * Propagated to {@code PowerOfAttorneyDTO.signingAuthorized} (BE-5c).
     */
    private Boolean signingAuthorized;

    /**
     * Whether this signer is a Politically Exposed Person.
     * Propagated to {@code PowerOfAttorneyDTO.isPep} (BE-5c).
     */
    private Boolean isPep;
}
