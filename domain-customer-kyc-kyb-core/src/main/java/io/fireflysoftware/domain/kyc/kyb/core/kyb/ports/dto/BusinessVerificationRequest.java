package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for business verification via third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationRequest {

    /** The party identifier of the business. */
    private UUID partyId;

    /** The registered company name. */
    private String companyName;

    /** The tax identification number (CIF/NIF). */
    private String taxId;

    /** The commercial registration number. */
    private String registrationNumber;

    /** The country of incorporation (ISO 3166-1 alpha-2). */
    private String country;
}
