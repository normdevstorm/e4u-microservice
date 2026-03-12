package com.e4u.learning_service.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for F-03 privacy consent step.
 * Must explicitly be set to true to accept consent.
 */
@Data
public class PrivacyConsentRequest {

    @NotNull(message = "Privacy consent must not be null")
    private Boolean privacyConsent;
}
