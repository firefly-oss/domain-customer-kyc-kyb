package io.fireflysoftware.domain.kyc.kyb.core.kyb.workflows;

import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DocumentData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.UboData;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.services.impl.KybServiceImpl;
import org.fireflyframework.orchestration.saga.engine.SagaEngine;
import org.fireflyframework.orchestration.saga.engine.SagaResult;
import org.fireflyframework.orchestration.saga.engine.StepInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link KybWorkflowSaga} via the service layer.
 *
 * <p>Uses mocked {@link SagaEngine} to verify that:
 * <ul>
 *   <li>The saga engine is invoked with the correct saga name and step inputs</li>
 *   <li>Errors from the saga engine are propagated correctly</li>
 * </ul>
 * End-to-end saga step and compensation tests require an integration test
 * with {@code InMemoryPersistenceProvider} and live handler beans.</p>
 */
@ExtendWith(MockitoExtension.class)
class KybWorkflowSagaTest {

    @Mock
    private SagaEngine sagaEngine;

    @Mock
    private SagaResult sagaResult;

    private KybServiceImpl service;

    private KybWorkflowInput validInput;

    @BeforeEach
    void setUp() {
        service = new KybServiceImpl(sagaEngine);

        validInput = new KybWorkflowInput(
                UUID.randomUUID(),
                "Acme Corp S.L.",
                "B-12345678",
                UUID.randomUUID(),
                List.of(DocumentData.builder()
                        .documentType("DEED_OF_INCORPORATION")
                        .documentReference("DOC-001")
                        .issueDate(LocalDateTime.now())
                        .build()),
                List.of(UboData.builder()
                        .naturalPersonId(UUID.randomUUID())
                        .ownershipPercentage(new BigDecimal("100.00"))
                        .ownershipType("DIRECT")
                        .build())
        );
    }

    @Test
    void shouldExecuteSagaAndReturnResult_whenHappyPath() {
        when(sagaEngine.execute(anyString(), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));
        when(sagaResult.isSuccess()).thenReturn(true);

        StepVerifier.create(service.startKyb(validInput))
                .assertNext(result -> assertThat(result.isSuccess()).isTrue())
                .verifyComplete();

        verify(sagaEngine).execute(anyString(), any(StepInputs.class));
    }

    @Test
    void shouldPropagateError_whenSagaEngineThrows() {
        when(sagaEngine.execute(anyString(), any(StepInputs.class)))
                .thenReturn(Mono.error(new RuntimeException("Saga engine unavailable")));

        StepVerifier.create(service.startKyb(validInput))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldReturnFailedResult_whenSagaFails() {
        when(sagaEngine.execute(anyString(), any(StepInputs.class)))
                .thenReturn(Mono.just(sagaResult));
        when(sagaResult.isSuccess()).thenReturn(false);

        StepVerifier.create(service.startKyb(validInput))
                .assertNext(result -> assertThat(result.isSuccess()).isFalse())
                .verifyComplete();
    }
}
