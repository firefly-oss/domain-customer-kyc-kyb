package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.CorporateDocumentsApi;
import io.fireflysoftware.core.kycb.sdk.model.CorporateDocumentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.SubmitCorporateDocumentsCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.mappers.KybMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitCorporateDocumentsHandlerTest {

    @Mock
    private CorporateDocumentsApi corporateDocumentsApi;

    private SubmitCorporateDocumentsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SubmitCorporateDocumentsHandler(corporateDocumentsApi, new KybMapperImpl());
    }

    @Test
    void shouldUploadAllDocumentsAndReturnIds() {
        UUID docId1 = UUID.randomUUID();
        UUID docId2 = UUID.randomUUID();
        UUID partyId = UUID.randomUUID();

        when(corporateDocumentsApi.addCorporateDocument(any(CorporateDocumentDTO.class), any()))
                .thenReturn(Mono.just(new CorporateDocumentDTO(null, null, docId1)))
                .thenReturn(Mono.just(new CorporateDocumentDTO(null, null, docId2)));

        DocumentData doc1 = DocumentData.builder()
                .documentType("DEED_OF_INCORPORATION")
                .documentReference("DOC-001")
                .issueDate(LocalDateTime.now())
                .build();
        DocumentData doc2 = DocumentData.builder()
                .documentType("MERCANTILE_REGISTRY")
                .documentReference("DOC-002")
                .issueDate(LocalDateTime.now())
                .build();

        SubmitCorporateDocumentsCommand cmd = SubmitCorporateDocumentsCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .documents(List.of(doc1, doc2))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .assertNext(result -> {
                    assertThat(result.ids()).hasSize(2);
                    assertThat(result.ids()).containsExactlyInAnyOrder(docId1, docId2);
                })
                .verifyComplete();

        verify(corporateDocumentsApi, times(2))
                .addCorporateDocument(any(CorporateDocumentDTO.class), any());
    }

    @Test
    void shouldPropagateErrorWhenOneDocumentFails() {
        UUID partyId = UUID.randomUUID();
        when(corporateDocumentsApi.addCorporateDocument(any(CorporateDocumentDTO.class), any()))
                .thenReturn(Mono.error(new RuntimeException("Upload failed")));

        DocumentData doc = DocumentData.builder()
                .documentType("DEED_OF_INCORPORATION")
                .documentReference("DOC-001")
                .issueDate(LocalDateTime.now())
                .build();

        SubmitCorporateDocumentsCommand cmd = SubmitCorporateDocumentsCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .documents(List.of(doc))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectError(RuntimeException.class)
                .verify();
    }
}
