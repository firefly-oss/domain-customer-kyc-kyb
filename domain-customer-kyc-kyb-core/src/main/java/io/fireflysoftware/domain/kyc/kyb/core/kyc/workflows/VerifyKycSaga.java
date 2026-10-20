package io.fireflysoftware.domain.kyc.kyb.core.kyc.workflows;

import io.fireflysoftware.core.kycb.sdk.api.RiskAssessmentApi;
import io.fireflysoftware.core.kycb.sdk.model.RiskAssessmentDTO;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.VerifyKycCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.orchestration.core.context.ExecutionContext;
import org.fireflyframework.orchestration.saga.annotation.Saga;
import org.fireflyframework.orchestration.saga.annotation.SagaStep;
import org.fireflyframework.orchestration.saga.annotation.StepEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.fireflysoftware.domain.kyc.kyb.core.utils.constants.KycKybConstants.*;

/**
 * Saga that orchestrates verifying a KYC case. First updates the verification
 * status to VERIFIED, then records a risk assessment for the party.
 */
@Saga(name = SAGA_VERIFY_KYC)
@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyKycSaga {

    private final CommandBus commandBus;
    private final RiskAssessmentApi riskAssessmentApi;

    /**
     * Updates the KYC verification status to VERIFIED via the command bus.
     *
     * @param cmd the verify-KYC command
     * @param ctx the saga execution context
     * @return an empty Mono on success
     */
    @SagaStep(id = STEP_UPDATE_VERIFICATION_STATUS)
    @StepEvent(type = EVENT_KYC_VERIFIED)
    public Mono<Void> updateVerificationStatus(VerifyKycCommand cmd, ExecutionContext ctx) {
        return commandBus.<Void>send(cmd);
    }

    /**
     * Records a risk assessment for the party after successful KYC verification.
     *
     * @param cmd the verify-KYC command containing party details
     * @param ctx the saga execution context
     * @return a Mono emitting the risk-assessment identifier
     */
    @SagaStep(id = STEP_RECORD_RISK_ASSESSMENT, dependsOn = STEP_UPDATE_VERIFICATION_STATUS)
    public Mono<UUID> recordRiskAssessment(VerifyKycCommand cmd, ExecutionContext ctx) {
        RiskAssessmentDTO riskDto = new RiskAssessmentDTO()
                .partyId(cmd.getPartyId())
                .assessmentType(ASSESSMENT_TYPE_KYC)
                .assessmentDate(LocalDateTime.now())
                .riskLevel(RISK_LEVEL_LOW)
                .riskScore(25)
                .riskCategory(RISK_CATEGORY_STANDARD);

        return riskAssessmentApi.createRiskAssessment(
                        cmd.getPartyId(), riskDto, UUID.randomUUID().toString())
                .map(RiskAssessmentDTO::getRiskAssessmentId);
    }
}
