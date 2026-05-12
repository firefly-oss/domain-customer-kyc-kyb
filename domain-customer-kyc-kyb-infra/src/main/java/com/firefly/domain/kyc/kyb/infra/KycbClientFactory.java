package com.firefly.domain.kyc.kyb.infra;

import com.firefly.core.kycb.sdk.api.ComplianceCasesApi;
import com.firefly.core.kycb.sdk.api.CorporateDocumentsApi;
import com.firefly.core.kycb.sdk.api.KybVerificationApi;
import com.firefly.core.kycb.sdk.api.KycVerificationApi;
import com.firefly.core.kycb.sdk.api.KycVerificationDocumentsApi;
import com.firefly.core.kycb.sdk.api.PowersOfAttorneyApi;
import com.firefly.core.kycb.sdk.api.RiskAssessmentApi;
import com.firefly.core.kycb.sdk.api.UboManagementApi;
import com.firefly.core.kycb.sdk.api.VerificationDocumentsApi;
import com.firefly.core.kycb.sdk.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates reactive API client beans for the core-common-kycb-mgmt downstream service.
 * All beans share a single {@link ApiClient} configured from {@link KycbClientConfigurationProperties}.
 */
@Component
public class KycbClientFactory {

    private static final int MAX_IN_MEMORY_SIZE = 20 * 1024 * 1024;

    private final ApiClient apiClient;

    public KycbClientFactory(KycbClientConfigurationProperties props) {
        WebClient webClient = ApiClient.buildWebClientBuilder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .build();
        this.apiClient = new ApiClient(webClient);
        this.apiClient.setBasePath(props.getBasePath());
    }

    /**
     * API for KYC verification lifecycle (open case, verify, fail, expire, renew).
     */
    @Bean
    public KycVerificationApi kycVerificationApi() {
        return new KycVerificationApi(apiClient);
    }

    /**
     * API for KYC identity document uploads and management.
     */
    @Bean
    public KycVerificationDocumentsApi kycVerificationDocumentsApi() {
        return new KycVerificationDocumentsApi(apiClient);
    }

    /**
     * API for KYB (Know Your Business) verification lifecycle.
     */
    @Bean
    public KybVerificationApi kybVerificationApi() {
        return new KybVerificationApi(apiClient);
    }

    /**
     * API for KYB corporate document uploads and management.
     */
    @Bean
    public CorporateDocumentsApi corporateDocumentsApi() {
        return new CorporateDocumentsApi(apiClient);
    }

    /**
     * API for compliance case management (shared by KYC and KYB flows).
     */
    @Bean
    public ComplianceCasesApi complianceCasesApi() {
        return new ComplianceCasesApi(apiClient);
    }

    /**
     * API for UBO (Ultimate Beneficial Owner) management in KYB flows.
     */
    @Bean
    public UboManagementApi uboManagementApi() {
        return new UboManagementApi(apiClient);
    }

    /**
     * API for Power-of-Attorney (authorized signer) management in KYB flows.
     */
    @Bean
    public PowersOfAttorneyApi powersOfAttorneyApi() {
        return new PowersOfAttorneyApi(apiClient);
    }

    /**
     * API for generic verification document management.
     */
    @Bean
    public VerificationDocumentsApi verificationDocumentsApi() {
        return new VerificationDocumentsApi(apiClient);
    }

    /**
     * API for party risk-assessment lifecycle (used by KYC verification flows).
     */
    @Bean
    public RiskAssessmentApi riskAssessmentApi() {
        return new RiskAssessmentApi(apiClient);
    }
}
