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


package io.fireflysoftware.domain.kyc.kyb.core.kyb.handlers;

import io.fireflysoftware.core.kycb.sdk.api.CorporateDocumentsApi;
import io.fireflysoftware.domain.kyc.kyb.core.kyb.commands.DeleteCorporateDocumentCommand;
import org.fireflyframework.cqrs.annotations.CommandHandlerComponent;
import org.fireflyframework.cqrs.command.CommandHandler;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Handler that deletes a corporate document via the core SDK (compensation).
 */
@CommandHandlerComponent
public class DeleteCorporateDocumentHandler extends CommandHandler<DeleteCorporateDocumentCommand, Void> {

    private final CorporateDocumentsApi corporateDocumentsApi;

    public DeleteCorporateDocumentHandler(CorporateDocumentsApi corporateDocumentsApi) {
        this.corporateDocumentsApi = corporateDocumentsApi;
    }

    @Override
    protected Mono<Void> doHandle(DeleteCorporateDocumentCommand cmd) {
        return corporateDocumentsApi.deleteCorporateDocument(cmd.getDocumentId(), UUID.randomUUID().toString()).then();
    }
}
