package com.firefly.domain.kyc.kyb.core.kyb.mappers;

import com.firefly.core.kycb.sdk.model.CorporateDocumentDTO;
import com.firefly.core.kycb.sdk.model.PowerOfAttorneyDTO;
import com.firefly.core.kycb.sdk.model.UboDTO;
import com.firefly.domain.kyc.kyb.core.kyb.commands.DocumentData;
import com.firefly.domain.kyc.kyb.core.kyb.commands.SignerData;
import com.firefly.domain.kyc.kyb.core.kyb.commands.UboData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * MapStruct mapper between domain value objects and SDK DTOs.
 *
 * <p>{@code partyId} is intentionally ignored in both mappings — it is
 * a required field on the DTOs that comes from the command context, not
 * from the value object itself. Handlers set it via the fluent setter
 * after calling the mapper.</p>
 *
 * <p>Read-only DTO constructor fields ({@code dateCreated}, {@code dateUpdated},
 * and the ID field) are set to {@code null} on creation — the core service
 * assigns them server-side.</p>
 */
@Mapper(componentModel = "spring")
public interface KybMapper {

    /**
     * Maps a {@link DocumentData} value object to a {@link CorporateDocumentDTO}.
     * The caller must set {@code partyId} on the returned DTO before the SDK call.
     */
    @Mapping(target = "partyId", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    CorporateDocumentDTO toDto(DocumentData data);

    /**
     * Maps a {@link UboData} value object to a {@link UboDTO}.
     * The caller must set {@code partyId} on the returned DTO before the SDK call.
     * If {@code startDate} is null, defaults to current timestamp.
     */
    @Mapping(target = "partyId", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "startDate",
             expression = "java(uboData.getStartDate() != null ? uboData.getStartDate() : java.time.LocalDateTime.now())")
    UboDTO toDto(UboData uboData);

    /**
     * Maps a {@link SignerData} value object to a {@link PowerOfAttorneyDTO}.
     *
     * <p>The caller must set {@code partyId} on the returned DTO before the
     * SDK call (it comes from the command context, not the value object).</p>
     *
     * <p>Field mapping notes:</p>
     * <ul>
     *   <li>{@code attorneyId} (caller-resolved natural-person UUID) → {@code attorneyId}.</li>
     *   <li>{@code role} → {@code powerType}, normalised to one of
     *       {@code GENERAL}, {@code LIMITED}, {@code SPECIAL}, {@code TRADING};
     *       unknown labels fall back to {@code GENERAL}.</li>
     *   <li>{@code powerDocumentReference} (String UUID) → {@code corporateDocumentId} (UUID).</li>
     *   <li>{@code email}, {@code signingAuthorized}, {@code isPep} pass through by name (BE-5c).</li>
     *   <li>Identity fields ({@code firstName}, {@code lastName}, {@code documentType},
     *       {@code documentNumber}) are not on the DTO — ignored at this mapping step.</li>
     * </ul>
     */
    @Mapping(target = "partyId", ignore = true)
    @Mapping(target = "powerType", source = "role", qualifiedByName = "roleToPowerType")
    @Mapping(target = "corporateDocumentId", source = "powerDocumentReference",
             qualifiedByName = "stringToUuid")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "isPoaCompleted", constant = "false")
    PowerOfAttorneyDTO toPowerOfAttorneyDto(SignerData data);

    /**
     * Converts a String UUID reference to a {@link UUID}, returning {@code null}
     * for null or blank input. Used for {@code corporateDocumentId} mapping.
     */
    @Named("stringToUuid")
    default UUID stringToUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return UUID.fromString(value);
    }

    /**
     * Normalises a free-form UI role label to a {@code PowerTypeEnum} key.
     * Accepted keys: {@code GENERAL}, {@code LIMITED}, {@code SPECIAL},
     * {@code TRADING}. Null, blank, or unrecognised labels fall back to
     * {@code GENERAL} so the core service does not reject the call.
     */
    @Named("roleToPowerType")
    default String roleToPowerType(String role) {
        if (role == null || role.isBlank()) {
            return "GENERAL";
        }
        String normalised = role.trim().toUpperCase();
        return switch (normalised) {
            case "GENERAL", "LIMITED", "SPECIAL", "TRADING" -> normalised;
            default -> "GENERAL";
        };
    }
}
