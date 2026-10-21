package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record DeleteKybVerificationCommand(UUID partyId, UUID verificationId) implements Command<Void> {
}
