package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Command to delete a corporate document (compensation).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCorporateDocumentCommand implements Command<Void> {

    private UUID documentId;
}
