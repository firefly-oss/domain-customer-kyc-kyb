package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybCaseQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKybCaseHandlerTest {

    @Mock
    private ComplianceCasesApi complianceCasesApi;

    private GetKybCaseHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetKybCaseHandler(complianceCasesApi);
    }

    @Test
    void shouldReturnCaseFromApi() {
        UUID caseId = UUID.randomUUID();
        ComplianceCaseDTO expectedDto = new ComplianceCaseDTO(null, null, caseId);
        expectedDto.caseType("KYB");

        when(complianceCasesApi.getComplianceCase(eq(caseId), any())).thenReturn(Mono.just(expectedDto));

        GetKybCaseQuery query = GetKybCaseQuery.builder().caseId(caseId).build();

        StepVerifier.create(handler.doHandle(query))
                .assertNext(dto -> assertThat(dto.getComplianceCaseId()).isEqualTo(caseId))
                .verifyComplete();

        verify(complianceCasesApi).getComplianceCase(eq(caseId), any());
    }

    @Test
    void shouldPropagateErrorWhenCaseNotFound() {
        UUID caseId = UUID.randomUUID();
        when(complianceCasesApi.getComplianceCase(eq(caseId), any()))
                .thenReturn(Mono.error(new RuntimeException("Case not found")));

        GetKybCaseQuery query = GetKybCaseQuery.builder().caseId(caseId).build();

        StepVerifier.create(handler.doHandle(query))
                .expectError(RuntimeException.class)
                .verify();
    }
}
