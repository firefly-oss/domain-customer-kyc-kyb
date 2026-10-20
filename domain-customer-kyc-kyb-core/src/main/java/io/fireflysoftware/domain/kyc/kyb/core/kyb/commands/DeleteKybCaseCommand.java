package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to delete (cancel) a KYB compliance case.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteKybCaseCommand implements Command<Void> {

    private UUID caseId;
}
