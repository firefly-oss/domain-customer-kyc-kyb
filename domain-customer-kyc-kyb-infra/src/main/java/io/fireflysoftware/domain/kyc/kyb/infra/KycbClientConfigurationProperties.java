package io.fireflysoftware.domain.kyc.kyb.infra;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the core-common-kycb-mgmt downstream client.
 * Bound from {@code api-configuration.core-platform.kycb-mgmt.*} in application.yaml.
 */
@ConfigurationProperties(prefix = "api-configuration.core-platform.kycb-mgmt")
@Data
public class KycbClientConfigurationProperties {

    /**
     * Base URL of the core-common-kycb-mgmt API.
     * Example: {@code http://localhost:8092}
     */
    private String basePath;

    /**
     * HTTP client timeout. Defaults to 10 seconds.
     */
    private Duration timeout = Duration.ofSeconds(10);
}
