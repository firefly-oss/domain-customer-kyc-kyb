package com.firefly.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Value object carrying Ultimate Beneficial Owner data into the KYB workflow.
 * Passed inside {@link RegisterUbosCommand} and mapped to {@code UboDTO}
 * by {@code KybMapper} before the SDK call.
 */
@Data
@Builder
public class UboData {

    private UUID naturalPersonId;
    private BigDecimal ownershipPercentage;

    /**
     * Ownership type. Constrained to DIRECT or INDIRECT to match the core
     * {@code UboDTO.OwnershipTypeEnum} contract.
     */
    @Pattern(regexp = "^(DIRECT|INDIRECT)$",
            message = "ownershipType must be DIRECT or INDIRECT")
    private String ownershipType;

    private String controlStructure;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String verificationMethod;
    private String titularidadRealDocument;

    /**
     * Optional contact email for the Ultimate Beneficial Owner.
     * Propagated to {@code UboDTO.email} on the core SDK call (BE-5d).
     */
    @Email(message = "email must be a valid email address")
    private String email;
}
