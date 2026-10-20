package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fireflyframework.cqrs.command.Command;

import java.util.UUID;

/**
 * Internal CQRS command to attach an evidence document to a KYB verification case.
 * This is the command-bus payload, not the public-facing DTO ({@link AttachEvidenceCommand}).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachKybEvidenceCommand implements Command<UUID> {

    /** The identifier of the organization party that owns the KYB case. */
    @NotNull
    private UUID partyId;

    /** The identifier of the KYB case to attach the document to. */
    @NotNull
    private UUID caseId;

    /** The type of the document (e.g. STATUTES, REGISTRY_EXTRACT, SHAREHOLDER_LIST). */
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
