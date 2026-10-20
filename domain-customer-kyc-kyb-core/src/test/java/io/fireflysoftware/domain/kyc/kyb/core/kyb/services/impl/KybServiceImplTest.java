package io.fireflysoftware.domain.kyc.kyb.core.kyb.services.impl;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UpdateCaseStatusCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows.KybWorkflowInput;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.fireflyframework.orchestration.saga.registry.SagaDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.kyb.constants.KybConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KybServiceImplTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private CommandBus commandBus;

    @Mock
    private SagaResult sagaResult;

    private KybServiceImpl kybService;

    @BeforeEach
    void setUp() {
        kybService = new KybServiceImpl(sagaEngine);
    }

    @Test
    void startKyb_executesSaga() {
        when(sagaEngine.execute(anyString(), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));
        when(sagaResult.isSuccess()).thenReturn(true);

        KybWorkflowInput input = new KybWorkflowInput(
                UUID.randomUUID(), "Acme Corp", "REG-123", UUID.randomUUID(),
                java.util.Collections.emptyList(), java.util.Collections.emptyList());

        StepVerifier.create(kybService.startKyb(input))
                .assertNext(result -> assertThat(result.isSuccess()).isTrue())
                .verifyComplete();

        verify(sagaEngine).execute(anyString(), any(StepInputs.class));
    }

    @Test
    void attachEvidence_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();
        AttachEvidenceCommand command = new AttachEvidenceCommand();
        command.setDocumentType("STATUTES");
        command.setDocumentName("company_statutes.pdf");

        StepVerifier.create(kybService.attachEvidence(caseId, command))
                .verifyComplete();
    }

    @Test
    void verifyKyb_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();

        StepVerifier.create(kybService.verifyKyb(caseId))
                .verifyComplete();
    }

    @Test
    void markDocsComplete_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();

        StepVerifier.create(kybService.markDocsComplete(caseId))
                .verifyComplete();
    }

    @Test
    void failKyb_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();

        StepVerifier.create(kybService.failKyb(caseId))
                .verifyComplete();
    }

    @Test
    void expireKyb_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();

        StepVerifier.create(kybService.expireKyb(caseId))
                .verifyComplete();
    }

    @Test
    void renewKyb_completesEmpty_notYetImplemented() {
        UUID caseId = UUID.randomUUID();

        StepVerifier.create(kybService.renewKyb(caseId))
                .verifyComplete();
    }
}
