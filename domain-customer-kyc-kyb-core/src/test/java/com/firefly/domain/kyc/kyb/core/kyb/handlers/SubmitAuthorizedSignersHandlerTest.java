package com.firefly.domain.kyc.kyb.core.kyb.handlers;

import com.firefly.core.kycb.sdk.api.PowersOfAttorneyApi;
import com.firefly.core.kycb.sdk.model.PowerOfAttorneyDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SignerData;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmitAuthorizedSignersCommand;
import com.firefly.domain.kyc.kyb.core.kyb.mappers.KybMapperImpl;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitAuthorizedSignersHandlerTest {

    @Mock
    private PowersOfAttorneyApi powersOfAttorneyApi;

    private SubmitAuthorizedSignersHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SubmitAuthorizedSignersHandler(powersOfAttorneyApi, new KybMapperImpl());
    }

    @Test
    void shouldSubmitAllSignersAndReturnIds() {
        UUID partyId = UUID.randomUUID();
        UUID poaId1 = UUID.randomUUID();
        UUID poaId2 = UUID.randomUUID();

        when(powersOfAttorneyApi.addPowerOfAttorney(any(PowerOfAttorneyDTO.class), any()))
                .thenReturn(Mono.just(new PowerOfAttorneyDTO(null, null, poaId1)))
                .thenReturn(Mono.just(new PowerOfAttorneyDTO(null, null, poaId2)));

        SignerData signer1 = SignerData.builder()
                .firstName("Alice")
                .lastName("Anderson")
                .documentType("DNI")
                .documentNumber("12345678A")
                .role("ADMIN")
                .signingAuthorized(true)
                .isPep(false)
                .attorneyId(UUID.randomUUID())
                .build();
        SignerData signer2 = SignerData.builder()
                .firstName("Bob")
                .lastName("Brown")
                .documentType("DNI")
                .documentNumber("87654321B")
                .role("DIRECTOR")
                .signingAuthorized(false)
                .isPep(true)
                .attorneyId(UUID.randomUUID())
                .build();

        SubmitAuthorizedSignersCommand cmd = SubmitAuthorizedSignersCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .signers(List.of(signer1, signer2))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .assertNext(result -> {
                    assertThat(result.ids()).hasSize(2);
                    assertThat(result.ids()).containsExactlyInAnyOrder(poaId1, poaId2);
                })
                .verifyComplete();

        verify(powersOfAttorneyApi, times(2))
                .addPowerOfAttorney(any(PowerOfAttorneyDTO.class), any());
    }

    @Test
    void shouldPropagateBe5cFieldsAndPartyIdToSdkCall() {
        UUID partyId = UUID.randomUUID();
        UUID poaId = UUID.randomUUID();
        UUID corporateDocumentId = UUID.randomUUID();

        ArgumentCaptor<PowerOfAttorneyDTO> dtoCaptor = ArgumentCaptor.forClass(PowerOfAttorneyDTO.class);
        ArgumentCaptor<String> idemCaptor = ArgumentCaptor.forClass(String.class);
        when(powersOfAttorneyApi.addPowerOfAttorney(dtoCaptor.capture(), idemCaptor.capture()))
                .thenReturn(Mono.just(new PowerOfAttorneyDTO(null, null, poaId)));

        SignerData signer = SignerData.builder()
                .firstName("Carol")
                .lastName("Castillo")
                .documentType("DNI")
                .documentNumber("11223344C")
                .role("ADMIN")
                .powerDocumentReference(corporateDocumentId.toString())
                .email("carol.castillo@example.com")
                .signingAuthorized(true)
                .isPep(false)
                .attorneyId(UUID.randomUUID())
                .build();

        SubmitAuthorizedSignersCommand cmd = SubmitAuthorizedSignersCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .signers(List.of(signer))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .assertNext(result -> assertThat(result.ids()).containsExactly(poaId))
                .verifyComplete();

        PowerOfAttorneyDTO captured = dtoCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("carol.castillo@example.com");
        assertThat(captured.getSigningAuthorized()).isTrue();
        assertThat(captured.getIsPep()).isFalse();
        assertThat(captured.getPartyId()).isEqualTo(partyId);
        assertThat(captured.getPowerType()).isEqualTo("GENERAL");
        assertThat(captured.getCorporateDocumentId()).isEqualTo(corporateDocumentId);
        assertThat(idemCaptor.getValue()).isNotNull();
        assertThat(idemCaptor.getValue()).isNotBlank();
    }

    @Test
    void shouldPropagateErrorWhenSignerSubmissionFails() {
        UUID partyId = UUID.randomUUID();
        when(powersOfAttorneyApi.addPowerOfAttorney(any(PowerOfAttorneyDTO.class), any()))
                .thenReturn(Mono.error(new RuntimeException("POA submission failed")));

        SignerData signer = SignerData.builder()
                .firstName("Dave")
                .lastName("Diaz")
                .documentType("DNI")
                .documentNumber("99887766D")
                .role("ADMIN")
                .signingAuthorized(true)
                .isPep(false)
                .attorneyId(UUID.randomUUID())
                .build();

        SubmitAuthorizedSignersCommand cmd = SubmitAuthorizedSignersCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .signers(List.of(signer))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldFailFastWhenAttorneyIdIsNull() {
        UUID partyId = UUID.randomUUID();

        SignerData signer = SignerData.builder()
                .firstName("Eve")
                .lastName("Evans")
                .documentType("DNI")
                .documentNumber("55667788E")
                .role("ADMIN")
                .signingAuthorized(true)
                .isPep(false)
                .build();

        SubmitAuthorizedSignersCommand cmd = SubmitAuthorizedSignersCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .signers(List.of(signer))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BusinessException.class);
                    BusinessException be = (BusinessException) error;
                    assertThat(be.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(be.getCode()).isEqualTo("ATTORNEY_ID_REQUIRED");
                    assertThat(be.getMessage()).contains("attorneyId");
                })
                .verify();

        verify(powersOfAttorneyApi, never())
                .addPowerOfAttorney(any(PowerOfAttorneyDTO.class), any());
    }
}
