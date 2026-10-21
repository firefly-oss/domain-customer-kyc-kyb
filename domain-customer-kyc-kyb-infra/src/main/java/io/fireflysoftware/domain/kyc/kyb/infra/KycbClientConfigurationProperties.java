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
