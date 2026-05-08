package com.firefly.domain.kyc.kyb.web.controller;

import com.firefly.core.kycb.sdk.model.ComplianceCaseDTO;
import com.firefly.core.kycb.sdk.model.KybVerificationDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.CreateKybCaseCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RegisterUbosCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.RequestKybVerificationCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmissionResult;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmitAuthorizedSignersCommand;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SubmitCorporateDocumentsCommand;
import com.firefly.domain.kyc.kyb.core.kyb.queries.GetKybCaseQuery;
import com.firefly.domain.kyc.kyb.core.kyb.queries.GetKybResultQuery;
import com.firefly.domain.kyc.kyb.interfaces.dtos.CreateKybCaseRequest;
import com.firefly.domain.kyc.kyb.interfaces.dtos.KybCaseResponse;
import com.firefly.domain.kyc.kyb.interfaces.dtos.KybResultResponse;
import com.firefly.domain.kyc.kyb.interfaces.dtos.RegisterUbosRequest;
import com.firefly.domain.kyc.kyb.interfaces.dtos.SubmitAuthorizedSignersRequest;
import com.firefly.domain.kyc.kyb.interfaces.dtos.SubmitCorporateDocumentsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.cqrs.command.CommandBus;
import org.fireflyframework.cqrs.query.QueryBus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller for KYB (Know Your Business) case management.
 * Handles corporate entity compliance workflows: case creation,
 * document submission, UBO registration, verification, and result retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kyb")
@RequiredArgsConstructor
@Tag(name = "KYB", description = "APIs for managing KYB compliance cases for legal entities")
public class KybController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @Operation(summary = "Create KYB Case",
               description = "Create a new KYB compliance case for a legal entity (persona jurídica).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "KYB case created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = KybCaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping(value = "/cases", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<KybCaseResponse>> createCase(
            @Valid @RequestBody CreateKybCaseRequest request) {
        log.info("Creating KYB case for partyId={}", request.getPartyId());
        CreateKybCaseCommand cmd = CreateKybCaseCommand.builder()
                .partyId(request.getPartyId())
                .businessName(request.getBusinessName())
                .registrationNumber(request.getRegistrationNumber())
                .tenantId(request.getTenantId())
                .build();
        return commandBus.<UUID>send(cmd)
                .map(caseId -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(KybCaseResponse.builder().caseId(caseId).build()));
    }

    @Operation(summary = "Get KYB Case",
               description = "Retrieve the current state of a KYB compliance case by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KYB case retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = KybCaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "KYB case not found", content = @Content)
    })
    @GetMapping(value = "/cases/{caseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<KybCaseResponse>> getCase(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId) {
        log.info("Retrieving KYB case: caseId={}", caseId);
        return queryBus.<ComplianceCaseDTO>query(GetKybCaseQuery.builder().caseId(caseId).build())
                .map(dto -> ResponseEntity.ok(toKybCaseResponse(dto)));
    }

    @Operation(summary = "Submit Corporate Documents",
               description = "Upload statutory and corporate documentation for a KYB case.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents submitted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubmissionResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid document data", content = @Content),
            @ApiResponse(responseCode = "404", description = "KYB case not found", content = @Content)
    })
    @PostMapping(value = "/cases/{caseId}/corporate-documents",
                 consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SubmissionResult>> submitCorporateDocuments(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId,
            @Valid @RequestBody SubmitCorporateDocumentsRequest request) {
        log.info("Submitting corporate documents: caseId={}, partyId={}", caseId, request.getPartyId());
        SubmitCorporateDocumentsCommand cmd = SubmitCorporateDocumentsCommand.builder()
                .caseId(caseId)
                .partyId(request.getPartyId())
                .documents(request.getDocuments())
                .build();
        return commandBus.<SubmissionResult>send(cmd)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Register UBOs",
               description = "Register Ultimate Beneficial Owners for a KYB case.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "UBOs registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubmissionResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid UBO data", content = @Content),
            @ApiResponse(responseCode = "404", description = "KYB case not found", content = @Content)
    })
    @PostMapping(value = "/cases/{caseId}/ubos",
                 consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SubmissionResult>> registerUbos(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId,
            @Valid @RequestBody RegisterUbosRequest request) {
        log.info("Registering UBOs: caseId={}, partyId={}, count={}", caseId, request.getPartyId(),
                request.getUbos().size());
        RegisterUbosCommand cmd = RegisterUbosCommand.builder()
                .caseId(caseId)
                .partyId(request.getPartyId())
                .ubos(request.getUbos())
                .build();
        return commandBus.<SubmissionResult>send(cmd)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Submit Authorized Signers",
               description = "Submit authorized signers (Powers of Attorney) for a KYB case (BE-5c).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorized signers submitted successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SubmissionResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid signer data", content = @Content),
            @ApiResponse(responseCode = "404", description = "KYB case not found", content = @Content)
    })
    @PostMapping(value = "/cases/{caseId}/authorized-signers",
                 consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<SubmissionResult>> submitAuthorizedSigners(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId,
            @Valid @RequestBody SubmitAuthorizedSignersRequest request) {
        log.info("Submitting authorized signers: caseId={}, partyId={}, count={}",
                caseId, request.getPartyId(), request.getSigners().size());
        SubmitAuthorizedSignersCommand cmd = SubmitAuthorizedSignersCommand.builder()
                .caseId(caseId)
                .partyId(request.getPartyId())
                .signers(request.getSigners())
                .build();
        return commandBus.<SubmissionResult>send(cmd)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Request KYB Verification",
               description = "Trigger external KYB verification for the case. Requires all documents and UBOs to be submitted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification requested successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = KybResultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Verification prerequisites not met", content = @Content),
            @ApiResponse(responseCode = "404", description = "KYB case not found", content = @Content)
    })
    @PostMapping(value = "/cases/{caseId}/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<KybResultResponse>> requestVerification(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId,
            @Parameter(description = "Party ID owning the case", required = true)
            @RequestParam UUID partyId) {
        log.info("Requesting KYB verification: caseId={}, partyId={}", caseId, partyId);
        RequestKybVerificationCommand cmd = RequestKybVerificationCommand.builder()
                .caseId(caseId)
                .partyId(partyId)
                .build();
        return commandBus.<KybVerificationDTO>send(cmd)
                .map(dto -> ResponseEntity.ok(toKybResultResponse(dto)));
    }

    @Operation(summary = "Get KYB Result",
               description = "Retrieve the final KYB verification result for a case.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KYB result retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = KybResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "Verification result not found", content = @Content)
    })
    @GetMapping(value = "/cases/{caseId}/result", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<KybResultResponse>> getResult(
            @Parameter(description = "Unique identifier of the KYB case", required = true)
            @PathVariable UUID caseId,
            @Parameter(description = "Party ID owning the case", required = true)
            @RequestParam UUID partyId,
            @Parameter(description = "KYB verification ID returned after the verification step", required = true)
            @RequestParam UUID verificationId) {
        log.info("Fetching KYB result: caseId={}, partyId={}, verificationId={}", caseId, partyId, verificationId);
        return queryBus.<KybVerificationDTO>query(
                        GetKybResultQuery.builder().partyId(partyId).verificationId(verificationId).build())
                .map(dto -> ResponseEntity.ok(toKybResultResponse(dto)));
    }

    private KybCaseResponse toKybCaseResponse(ComplianceCaseDTO dto) {
        return KybCaseResponse.builder()
                .caseId(dto.getComplianceCaseId())
                .partyId(dto.getPartyId())
                .caseType(dto.getCaseType())
                .caseStatus(dto.getCaseStatus())
                .casePriority(dto.getCasePriority())
                .caseReference(dto.getCaseReference())
                .caseSummary(dto.getCaseSummary())
                .assignedTo(dto.getAssignedTo())
                .dueDate(dto.getDueDate())
                .resolutionDate(dto.getResolutionDate())
                .resolutionNotes(dto.getResolutionNotes())
                .build();
    }

    private KybResultResponse toKybResultResponse(KybVerificationDTO dto) {
        return KybResultResponse.builder()
                .verificationId(dto.getKybVerificationId())
                .partyId(dto.getPartyId())
                .verificationStatus(dto.getVerificationStatus())
                .riskScore(dto.getRiskScore())
                .riskLevel(dto.getRiskLevel())
                .verificationDate(dto.getVerificationDate())
                .nextReviewDate(dto.getNextReviewDate())
                .mercantileRegistryVerified(dto.getMercantileRegistryVerified())
                .deedOfIncorporationVerified(dto.getDeedOfIncorporationVerified())
                .businessStructureVerified(dto.getBusinessStructureVerified())
                .uboVerified(dto.getUboVerified())
                .taxIdVerified(dto.getTaxIdVerified())
                .operatingLicenseVerified(dto.getOperatingLicenseVerified())
                .verificationNotes(dto.getVerificationNotes())
                .build();
    }
}
