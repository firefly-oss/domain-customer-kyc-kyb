package com.firefly.domain.kyc.kyb.core.kyb.handlers;

import com.firefly.core.kycb.sdk.api.UboManagementApi;
import com.firefly.core.kycb.sdk.model.UboDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RegisterUbosCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.UboData;
import com.firefly.domain.kyc.kyb.core.kyb.mappers.KybMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUbosHandlerTest {

    @Mock
    private UboManagementApi uboManagementApi;

    private RegisterUbosHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterUbosHandler(uboManagementApi, new KybMapperImpl());
    }

    @Test
    void shouldRegisterAllUbosAndReturnIds() {
        UUID partyId = UUID.randomUUID();
        UUID uboId1 = UUID.randomUUID();
        UUID uboId2 = UUID.randomUUID();

        when(uboManagementApi.addUbo(eq(partyId), any(UboDTO.class), any()))
                .thenReturn(Mono.just(new UboDTO(null, null, uboId1)))
                .thenReturn(Mono.just(new UboDTO(null, null, uboId2)));

        UboData ubo1 = UboData.builder()
                .naturalPersonId(UUID.randomUUID())
                .ownershipPercentage(new BigDecimal("60.00"))
                .ownershipType("DIRECT")
                .build();
        UboData ubo2 = UboData.builder()
                .naturalPersonId(UUID.randomUUID())
                .ownershipPercentage(new BigDecimal("40.00"))
                .ownershipType("INDIRECT")
                .build();

        RegisterUbosCommand cmd = RegisterUbosCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .ubos(List.of(ubo1, ubo2))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .assertNext(result -> {
                    assertThat(result.ids()).hasSize(2);
                    assertThat(result.ids()).containsExactlyInAnyOrder(uboId1, uboId2);
                })
                .verifyComplete();

        verify(uboManagementApi, times(2)).addUbo(eq(partyId), any(UboDTO.class), any());
    }

    @Test
    void shouldPropagateEmailAndOwnershipTypeToSdkCall() {
        UUID partyId = UUID.randomUUID();
        UUID uboId = UUID.randomUUID();

        ArgumentCaptor<UboDTO> dtoCaptor = ArgumentCaptor.forClass(UboDTO.class);
        when(uboManagementApi.addUbo(eq(partyId), dtoCaptor.capture(), any()))
                .thenReturn(Mono.just(new UboDTO(null, null, uboId)));

        UboData ubo = UboData.builder()
                .naturalPersonId(UUID.randomUUID())
                .ownershipPercentage(new BigDecimal("75.00"))
                .ownershipType("DIRECT")
                .email("ubo.contact@example.com")
                .build();

        RegisterUbosCommand cmd = RegisterUbosCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .ubos(List.of(ubo))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .assertNext(result -> assertThat(result.ids()).containsExactly(uboId))
                .verifyComplete();

        UboDTO captured = dtoCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("ubo.contact@example.com");
        assertThat(captured.getOwnershipType()).isEqualTo("DIRECT");
        assertThat(captured.getPartyId()).isEqualTo(partyId);
    }

    @Test
    void shouldPropagateErrorWhenUboRegistrationFails() {
        UUID partyId = UUID.randomUUID();
        when(uboManagementApi.addUbo(any(), any(UboDTO.class), any()))
                .thenReturn(Mono.error(new RuntimeException("UBO registration failed")));

        UboData ubo = UboData.builder()
                .naturalPersonId(UUID.randomUUID())
                .ownershipPercentage(new BigDecimal("100.00"))
                .ownershipType("DIRECT")
                .build();

        RegisterUbosCommand cmd = RegisterUbosCommand.builder()
                .caseId(UUID.randomUUID())
                .partyId(partyId)
                .ubos(List.of(ubo))
                .build();

        StepVerifier.create(handler.doHandle(cmd))
                .expectError(RuntimeException.class)
                .verify();
    }
}
