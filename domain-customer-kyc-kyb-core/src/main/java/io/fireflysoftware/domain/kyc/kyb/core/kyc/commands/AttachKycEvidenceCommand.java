package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Internal CQRS command to attach an evidence document to a KYC verification case.
 * This is the command-bus payload, not the public-facing DTO ({@link AttachEvidenceCommand}).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachKycEvidenceCommand implements Command<UUID> {

    /** The identifier of the party that owns the KYC case. */
    @NotNull
    private UUID partyId;

    /** The identifier of the KYC case to attach the document to. */
    @NotNull
    private UUID caseId;

    /** The type of the document (e.g. PASSPORT, DRIVERS_LICENSE, UTILITY_BILL). */
    @NotBlank
    private String documentType;

    /** The human-readable name of the document. */
    private String documentName;

    /** The Base64-encoded document content. */
    private String documentContent;

    /** The MIME type of the document. */
    private String mimeType;

    /** The size of the document in bytes. */
    private Long documentSize;

    /** The fingerprint reference for the document. */
    @NotBlank
    private String fingerprint;

    /** An optional description of the document. */
    private String description;
}
