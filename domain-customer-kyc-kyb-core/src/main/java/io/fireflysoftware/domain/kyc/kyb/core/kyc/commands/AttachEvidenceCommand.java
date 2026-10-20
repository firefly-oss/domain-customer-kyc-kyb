package io.fireflysoftware.domain.kyc.kyb.core.kyc.commands;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachEvidenceCommand {
    
    private UUID caseId;
    private String documentType;
    private String documentName;
    private String documentContent;
    private String mimeType;
    private Long documentSize;
    private String fingerprint;
    private String description;
}