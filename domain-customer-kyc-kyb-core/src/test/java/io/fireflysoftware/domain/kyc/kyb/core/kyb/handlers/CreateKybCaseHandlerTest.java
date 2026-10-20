package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.ComplianceCasesApi;
import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateKybCaseHandlerTest {

    @Mock
    private ComplianceCasesApi complianceCasesApi;

    private CreateKybCaseHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateKybCaseHandler(complianceCasesApi);
    }

    @Test
    void shouldCreateCaseAndReturnCaseId() {
        UUID expectedCaseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();

        ComplianceCaseDTO responseDto = new ComplianceCaseDTO(null, null, expectedCaseId);
        when(complianceCasesApi.createComplianceCase(any(ComplianceCaseDTO.class), any()))
                .thenReturn(Mono.just(responseDto));

        CreateKybCaseCommand cmd = CreateKybCaseCommand.builder()
                .partyId(partyId)
                .businessName("Acme Corp")
                .registrationNumber("REG-001")
                .tenantId(UUID.randomUUID())
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectNext(expectedCaseId)
                .verifyComplete();

        verify(complianceCasesApi).createComplianceCase(any(ComplianceCaseDTO.class), any());
    }

    @Test
    void shouldFailWhenApiReturnsNullCaseId() {
        UUID partyId = UUID.randomUUID();
        ComplianceCaseDTO responseDto = new ComplianceCaseDTO(null, null, null);
        when(complianceCasesApi.createComplianceCase(any(ComplianceCaseDTO.class), any()))
                .thenReturn(Mono.just(responseDto));

        CreateKybCaseCommand cmd = CreateKybCaseCommand.builder()
                .partyId(partyId)
                .businessName("Acme Corp")
                .registrationNumber("REG-001")
                .tenantId(UUID.randomUUID())
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectError(NullPointerException.class)
                .verify();
    }

    @Test
    void shouldPropagateApiError() {
        UUID partyId = UUID.randomUUID();
        when(complianceCasesApi.createComplianceCase(any(ComplianceCaseDTO.class), any()))
                .thenReturn(Mono.error(new RuntimeException("Core service unavailable")));

        CreateKybCaseCommand cmd = CreateKybCaseCommand.builder()
                .partyId(partyId)
                .businessName("Acme Corp")
                .registrationNumber("REG-001")
                .tenantId(UUID.randomUUID())
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectError(RuntimeException.class)
                .verify();
    }
}
