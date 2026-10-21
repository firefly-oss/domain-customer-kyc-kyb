/*
 * Copyright 2025 Firefly Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.fireflysoftware.domain.kyc.kyb.web.controller;

import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.AttachEvidenceCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.commands.OpenKycCaseCommand;
import io.fireflysoftware.domain.kyc.kyb.core.kyc.services.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC", description = "CQ queries and registration for kyc")
public class KycController {

    private final KycService kycService;

    @PostMapping("/cases")
    @Operation(summary = "Start KYC", description = "Open a KYC case with required due diligence level")
    public Mono<ResponseEntity<Object>> openCase(@RequestBody OpenKycCaseCommand command) {
        return kycService.openCase(command)
                .map(result -> {
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("caseId", result.resultOf("create-kyc-verification", UUID.class).orElse(null));
                    return ResponseEntity.ok((Object) body);
                });
    }

    @PostMapping("/cases/{caseId}/evidence")
    @Operation(summary = "Attach Evidence", description = "Attach identity/POA/selfie documents (fingerprint ref)")
    public Mono<ResponseEntity<Object>> attachEvidence(@PathVariable UUID caseId, @RequestBody AttachEvidenceCommand command) {
        return kycService.attachEvidence(caseId, command)
                .map(result -> {
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("documentId", result.resultOf("upload-document", UUID.class).orElse(null));
                    return ResponseEntity.ok((Object) body);
                });
    }

    @PostMapping("/cases/{caseId}/docs-complete")
    @Operation(summary = "Mark Documents Complete", description = "Mark dossier complete for review")
    public Mono<ResponseEntity<Object>> markDocsComplete(@PathVariable UUID caseId, @RequestParam UUID partyId) {
        return kycService.markDocsComplete(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/pep-adverse-media")
    @Operation(summary = "Record PEP/Adverse Media", description = "Record PEP/adverse media status affecting decision")
    public Mono<ResponseEntity<Object>> recordPepAdverseMedia(@PathVariable UUID caseId, @RequestParam UUID partyId) {
        return kycService.recordPepAdverseMedia(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/verify")
    @Operation(summary = "Verify KYC", description = "Approve KYC at level and set expiration")
    public Mono<ResponseEntity<Object>> verify(@PathVariable UUID caseId,
                                               @RequestParam(required = false) UUID partyId) {
        return kycService.verify(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/fail")
    @Operation(summary = "Fail KYC", description = "Close as rejected (fraud/inconsistency)")
    public Mono<ResponseEntity<Object>> fail(@PathVariable UUID caseId, @RequestParam UUID partyId) {
        return kycService.fail(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/expire")
    @Operation(summary = "Expire KYC", description = "Mark verification expired by time")
    public Mono<ResponseEntity<Object>> expire(@PathVariable UUID caseId, @RequestParam UUID partyId) {
        return kycService.expire(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }

    @PostMapping("/cases/{caseId}/renew")
    @Operation(summary = "Renew KYC", description = "Renew verification with new level/expiry")
    public Mono<ResponseEntity<Object>> renew(@PathVariable UUID caseId, @RequestParam UUID partyId) {
        return kycService.renew(caseId, partyId)
                .map(result -> ResponseEntity.ok().build());
    }
}
