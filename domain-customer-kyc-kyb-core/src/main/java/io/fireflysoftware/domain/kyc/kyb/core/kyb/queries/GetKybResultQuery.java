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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.queries;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.fireflyframework.cqrs.query.Query;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;

import java.util.UUID;

/**
 * Retrieves the KYB verification result by party and verification identifiers.
 *
 * <p>Both IDs are stored in {@code SagaContext} after the
 * {@code requestVerification} step completes and can be used by downstream
 * consumers to fetch the final verification outcome.</p>
 */
@Data
@Builder
public class GetKybResultQuery implements Query<KybVerificationDTO> {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotNull(message = "Verification ID is required")
    private final UUID verificationId;

    @Override
    public String getCacheKey() {
        return "KybResult:" + partyId + ":" + verificationId;
    }
}
