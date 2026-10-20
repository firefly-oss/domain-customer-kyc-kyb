package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import io.fireflysoftware.core.kycb.sdk.model.ComplianceCaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to update the status of a KYB compliance case.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCaseStatusCommand implements Command<ComplianceCaseDTO> {

    private UUID caseId;
    private String newStatus;
}
