package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record DeleteKycEvidenceCommand(UUID partyId, UUID verificationId, UUID documentId) implements Command<Void> {
}
