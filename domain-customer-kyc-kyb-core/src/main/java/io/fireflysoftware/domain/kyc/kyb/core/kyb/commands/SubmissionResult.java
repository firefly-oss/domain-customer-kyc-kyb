package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import java.util.List;
import java.util.UUID;

/**
 * Typed wrapper for a list of created resource IDs returned by batch-creation
 * command handlers (document upload, UBO registration).
 *
 * <p>Using a dedicated wrapper instead of {@code Command<List<UUID>>} avoids
 * Java generic type-erasure issues in the CQRS handler auto-discovery
 * mechanism which relies on reflection over the raw type parameter.</p>
 */
public record SubmissionResult(List<UUID> ids) {}
