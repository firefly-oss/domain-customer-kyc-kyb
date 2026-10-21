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

/**
 * Result of a business verification from a third-party KYB provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResult {

    /** Whether the business passed verification. */
    private boolean verified;

    /** Confidence score from 0 to 100. */
    private int score;

    /** Risk level determined by the provider. */
    private String riskLevel;

    /** Additional details from the provider. */
    private String details;

    /** The provider that performed the verification. */
    private String provider;
}
