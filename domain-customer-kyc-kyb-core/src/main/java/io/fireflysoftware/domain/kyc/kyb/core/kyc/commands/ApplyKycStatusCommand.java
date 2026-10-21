package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyKycStatusCommand implements Command<Void> {
    private UUID partyId;
    private UUID caseId;
    private String status;
    private String reason;
}
