package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import io.fireflysoftware.core.kycb.sdk.model.KybVerificationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to record a KYB verification result in the core layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordKybVerificationCommand implements Command<KybVerificationDTO> {

    private UUID caseId;
    private String verificationStatus;
}
