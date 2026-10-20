package io.fireflysoftware.domain.kyc.kyb.core.kyb.ports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for UBO (Ultimate Beneficial Owner) verification via third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UboVerificationRequest {

    /** The UBO identifier. */
    private UUID uboId;

    /** The full name of the UBO. */
    private String fullName;

    /** The identity document number. */
    private String documentNumber;

    /** The ownership percentage. */
    private BigDecimal ownershipPercentage;
}
