package io.fireflysoftware.domain.kyc.kyb.core.kyb.commands;

import io.fireflysoftware.core.kycb.sdk.model.CorporateDocumentDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fireflyframework.cqrs.command.Command;

/**
 * Command to add a corporate document to a KYB case.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class AddCorporateDocumentCommand extends CorporateDocumentDTO implements Command<CorporateDocumentDTO> {
}
