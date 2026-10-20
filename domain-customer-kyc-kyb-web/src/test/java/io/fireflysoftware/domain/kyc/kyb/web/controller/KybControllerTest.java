package io.fireflysoftware.domain.kyc.kyb.web.controller;

import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RegisterUbosCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.RequestKybVerificationCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmitCorporateDocumentsCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UboData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybCaseQuery;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybResultQuery;
import io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.CreateKybCaseRequest;
import io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.KybCaseResponse;
import io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.KybResultResponse;
import io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.RegisterUbosRequest;
import io.fireflysoftware.domain.kyc.kyb.interfaces.dtos.SubmitCorporateDocumentsRequest;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KybControllerTest {

    @Mock
    private CommandBus commandBus;

    @Mock
    private QueryBus queryBus;

    private KybController controller;

    @BeforeEach
    void setUp() {
        controller = new KybController(commandBus, queryBus);
    }

    @Test
    void createCase_shouldReturn201_withCaseId() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(commandBus.send(any(CreateKybCaseCommand.class))).thenReturn(Mono.just(caseId));

        CreateKybCaseRequest request = CreateKybCaseRequest.builder()
                .partyId(partyId)
                .businessName("Acme Corp S.L.")
                .registrationNumber("B-12345678")
                .tenantId(tenantId)
                .build();

        StepVerifier.create(controller.createCase(request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getCaseId()).isEqualTo(caseId);
                })
                .verifyComplete();

        verify(commandBus).send(any(CreateKybCaseCommand.class));
    }

    @Test
    void createCase_shouldPropagateCommandBusError() {
        UUID partyId = UUID.randomUUID();

        when(commandBus.send(any(CreateKybCaseCommand.class)))
                .thenReturn(Mono.error(new RuntimeException("Core service unavailable")));

        CreateKybCaseRequest request = CreateKybCaseRequest.builder()
                .partyId(partyId)
                .businessName("Acme Corp")
                .registrationNumber("B-001")
                .tenantId(UUID.randomUUID())
                .build();

        StepVerifier.create(controller.createCase(request))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getCase_shouldReturn200_withMappedCaseResponse() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();

        ComplianceCaseDTO dto = new ComplianceCaseDTO(null, null, caseId);
        dto.partyId(partyId);
        dto.caseType("KYB");
        dto.caseStatus("OPEN");
        dto.casePriority("MEDIUM");
        dto.caseReference("B-12345678");
        dto.caseSummary("Acme Corp KYB case");
        dto.assignedTo("SYSTEM");
        dto.dueDate(LocalDateTime.now().plusDays(30));

        when(queryBus.query(any(GetKybCaseQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getCase(caseId))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    KybCaseResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getCaseId()).isEqualTo(caseId);
                    assertThat(body.getPartyId()).isEqualTo(partyId);
                    assertThat(body.getCaseStatus()).isEqualTo("OPEN");
                    assertThat(body.getCaseType()).isEqualTo("KYB");
                })
                .verifyComplete();

        verify(queryBus).query(any(GetKybCaseQuery.class));
    }

    @Test
    void submitCorporateDocuments_shouldReturn200_withSubmissionResult() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        List<UUID> documentIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        SubmissionResult result = new SubmissionResult(documentIds);

        when(commandBus.send(any(SubmitCorporateDocumentsCommand.class))).thenReturn(Mono.just(result));

        SubmitCorporateDocumentsRequest request = SubmitCorporateDocumentsRequest.builder()
                .partyId(partyId)
                .documents(List.of(
                        DocumentData.builder()
                                .documentType("DEED_OF_INCORPORATION")
                                .documentReference("DOC-001")
                                .documentSystemId("SYS-001")
                                .build()))
                .build();

        StepVerifier.create(controller.submitCorporateDocuments(caseId, request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().ids()).hasSize(2);
                })
                .verifyComplete();

        verify(commandBus).send(any(SubmitCorporateDocumentsCommand.class));
    }

    @Test
    void registerUbos_shouldReturn200_withSubmissionResult() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        List<UUID> uboIds = List.of(UUID.randomUUID());
        SubmissionResult result = new SubmissionResult(uboIds);

        when(commandBus.send(any(RegisterUbosCommand.class))).thenReturn(Mono.just(result));

        RegisterUbosRequest request = RegisterUbosRequest.builder()
                .partyId(partyId)
                .ubos(List.of(
                        UboData.builder()
                                .naturalPersonId(UUID.randomUUID())
                                .ownershipPercentage(new BigDecimal("51.00"))
                                .ownershipType("DIRECT")
                                .build()))
                .build();

        StepVerifier.create(controller.registerUbos(caseId, request))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().ids()).hasSize(1);
                })
                .verifyComplete();

        verify(commandBus).send(any(RegisterUbosCommand.class));
    }

    @Test
    void requestVerification_shouldReturn200_withKybResultResponse() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        KybVerificationDTO dto = new KybVerificationDTO(null, null, verificationId);
        dto.partyId(partyId);
        dto.verificationStatus("VERIFIED");
        dto.riskScore(25);
        dto.riskLevel("LOW");
        dto.verificationDate(LocalDateTime.now());
        dto.mercantileRegistryVerified(true);
        dto.deedOfIncorporationVerified(true);
        dto.businessStructureVerified(true);
        dto.uboVerified(true);
        dto.taxIdVerified(true);
        dto.operatingLicenseVerified(false);

        when(commandBus.send(any(RequestKybVerificationCommand.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.requestVerification(caseId, partyId))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    KybResultResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getVerificationId()).isEqualTo(verificationId);
                    assertThat(body.getPartyId()).isEqualTo(partyId);
                    assertThat(body.getVerificationStatus()).isEqualTo("VERIFIED");
                    assertThat(body.getRiskScore()).isEqualTo(25);
                    assertThat(body.getRiskLevel()).isEqualTo("LOW");
                    assertThat(body.getUboVerified()).isTrue();
                })
                .verifyComplete();

        verify(commandBus).send(any(RequestKybVerificationCommand.class));
    }

    @Test
    void getResult_shouldReturn200_withKybResultResponse() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        KybVerificationDTO dto = new KybVerificationDTO(null, null, verificationId);
        dto.partyId(partyId);
        dto.verificationStatus("VERIFIED");
        dto.riskScore(10);
        dto.riskLevel("LOW");
        dto.verificationDate(LocalDateTime.now());
        dto.nextReviewDate(LocalDateTime.now().plusYears(1));
        dto.mercantileRegistryVerified(true);
        dto.deedOfIncorporationVerified(true);
        dto.businessStructureVerified(true);
        dto.uboVerified(true);
        dto.taxIdVerified(true);
        dto.operatingLicenseVerified(true);

        when(queryBus.query(any(GetKybResultQuery.class))).thenReturn(Mono.just(dto));

        StepVerifier.create(controller.getResult(caseId, partyId, verificationId))
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                    KybResultResponse body = response.getBody();
                    assertThat(body).isNotNull();
                    assertThat(body.getVerificationId()).isEqualTo(verificationId);
                    assertThat(body.getVerificationStatus()).isEqualTo("VERIFIED");
                    assertThat(body.getNextReviewDate()).isNotNull();
                })
                .verifyComplete();

        verify(queryBus).query(any(GetKybResultQuery.class));
    }

    @Test
    void getResult_shouldPropagateQueryBusError() {
        UUID caseId = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        when(queryBus.query(any(GetKybResultQuery.class)))
                .thenReturn(Mono.error(new RuntimeException("Verification not found")));

        StepVerifier.create(controller.getResult(caseId, partyId, verificationId))
                .expectError(RuntimeException.class)
                .verify();
    }
}
