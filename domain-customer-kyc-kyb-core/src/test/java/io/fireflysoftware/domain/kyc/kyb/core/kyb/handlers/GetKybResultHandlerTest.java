package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.KybVerificationApi;
import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.queries.GetKybResultQuery;
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
class GetKybResultHandlerTest {

    @Mock
    private KybVerificationApi kybVerificationApi;

    private GetKybResultHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetKybResultHandler(kybVerificationApi);
    }

    @Test
    void shouldReturnVerificationDtoFromApi() {
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        KybVerificationDTO expectedDto = new KybVerificationDTO(null, null, verificationId);
        expectedDto.verificationStatus("VERIFIED");
        when(kybVerificationApi.getKybVerification(eq(partyId), eq(verificationId), any()))
                .thenReturn(Mono.just(expectedDto));

        GetKybResultQuery query = GetKybResultQuery.builder()
                .partyId(partyId)
                .verificationId(verificationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .assertNext(dto -> {
                    assertThat(dto.getKybVerificationId()).isEqualTo(verificationId);
                    assertThat(dto.getVerificationStatus()).isEqualTo("VERIFIED");
                })
                .verifyComplete();

        verify(kybVerificationApi).getKybVerification(eq(partyId), eq(verificationId), any());
    }

    @Test
    void shouldPropagateErrorWhenVerificationNotFound() {
        UUID partyId = UUID.randomUUID();
        UUID verificationId = UUID.randomUUID();

        when(kybVerificationApi.getKybVerification(eq(partyId), eq(verificationId), any()))
                .thenReturn(Mono.error(new RuntimeException("Verification not found")));

        GetKybResultQuery query = GetKybResultQuery.builder()
                .partyId(partyId)
                .verificationId(verificationId)
                .build();

        StepVerifier.create(handler.doHandle(query))
                .expectError(RuntimeException.class)
                .verify();
    }
}
