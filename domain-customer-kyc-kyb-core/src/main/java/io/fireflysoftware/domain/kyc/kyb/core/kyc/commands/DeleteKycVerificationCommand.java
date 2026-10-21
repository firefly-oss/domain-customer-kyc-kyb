package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

public record DeleteKycVerificationCommand(UUID partyId, UUID verificationId) implements Command<Void> {
}
