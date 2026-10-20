package io.fireflysoftware.domain.kyc.kyb.web.controller;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.services.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC", description = "CQ queries and registration for kyc")
public class KycController {

    private final KycService kycService;

    @PostMapping("/cases")
    @Operation(summary = "Start KYC", description = "Open a KYC case with required due diligence level")
    public Mono<ResponseEntity<Object>> openCase() {
        return kycService.openCase()
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/evidence")
    @Operation(summary = "Attach Evidence", description = "Attach identity/POA/selfie documents (fingerprint ref)")
    public Mono<ResponseEntity<Object>> attachEvidence(@PathVariable UUID caseId, @RequestBody AttachEvidenceCommand command) {
        return kycService.attachEvidence(caseId, command)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/docs-complete")
    @Operation(summary = "Mark Documents Complete", description = "Mark dossier complete for review")
    public Mono<ResponseEntity<Object>> markDocsComplete(@PathVariable UUID caseId) {
        return kycService.markDocsComplete(caseId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/pep-adverse-media")
    @Operation(summary = "Record PEP/Adverse Media", description = "Record PEP/adverse media status affecting decision")
    public Mono<ResponseEntity<Object>> recordPepAdverseMedia(@PathVariable UUID caseId) {
        return kycService.recordPepAdverseMedia(caseId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/verify")
    @Operation(summary = "Verify KYC", description = "Approve KYC at level and set expiration")
    public Mono<ResponseEntity<Object>> verify(@PathVariable UUID caseId) {
        return kycService.verify(caseId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/fail")
    @Operation(summary = "Fail KYC", description = "Close as rejected (fraud/inconsistency)")
    public Mono<ResponseEntity<Object>> fail(@PathVariable UUID caseId) {
        return kycService.fail(caseId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/expire")
    @Operation(summary = "Expire KYC", description = "Mark verification expired by time")
    public Mono<ResponseEntity<Object>> expire(@PathVariable UUID caseId) {
        return kycService.expire(caseId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/renew")
    @Operation(summary = "Renew KYC", description = "Renew verification with new level/expiry")
    public Mono<ResponseEntity<Object>> renew(@PathVariable UUID caseId) {
        return kycService.renew(caseId)
                .map(result -> ResponseEntity.ok().build());
    }
}
