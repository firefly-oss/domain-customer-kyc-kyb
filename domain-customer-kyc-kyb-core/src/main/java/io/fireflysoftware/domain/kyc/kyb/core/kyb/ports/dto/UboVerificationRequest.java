/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
