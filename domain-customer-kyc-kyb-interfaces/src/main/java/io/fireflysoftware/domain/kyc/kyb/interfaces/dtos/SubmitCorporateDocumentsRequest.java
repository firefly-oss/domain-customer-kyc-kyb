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


package io.fireflysoftware.domain.kyc.kyb.interfaces.dtos;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request body for uploading a batch of corporate documents to a KYB case.
 * The {@code caseId} is taken from the path variable; {@code partyId} is
 * provided here to authorise the upload against the owning party.
 */
@Data
@Builder
public class SubmitCorporateDocumentsRequest {

    @NotNull(message = "Party ID is required")
    private final UUID partyId;

    @NotEmpty(message = "At least one document is required")
    private final List<DocumentData> documents;
}
