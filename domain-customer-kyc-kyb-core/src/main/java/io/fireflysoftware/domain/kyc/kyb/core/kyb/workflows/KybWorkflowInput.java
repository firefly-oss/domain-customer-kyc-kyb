package io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UboData;

import java.util.List;
import java.util.UUID;

/**
 * Immutable input record for the {@link KybWorkflowSaga}.
 *
 * <p>Carries all data required by every saga step. Step 1 stores documents
 * and UBOs in {@code SagaContext} so that steps 2 and 3 can retrieve them
 * without relying on the injected {@code cmd} parameter, which may be null
 * in downstream steps due to ArgumentResolver behaviour.</p>
 */
public record KybWorkflowInput(
        UUID partyId,
        String businessName,
        String registrationNumber,
        UUID tenantId,
        List<DocumentData> documents,
        List<UboData> ubos
) {}
