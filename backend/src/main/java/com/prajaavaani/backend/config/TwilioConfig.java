package com.prajaavaani.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "twilio") // Matches the prefix in application.properties
@Data // Lombok for getters/setters
@Validated // Enable validation on properties
public class TwilioConfig {

    @NotBlank(message = "Twilio Account SID is required")
    private String accountSid;

    @NotBlank(message = "Twilio Auth Token is required")
    private String authToken;

    @NotBlank(message = "Twilio Phone Number is required")
    private String phoneNumber; // Or messagingServiceSid if using that

    // Optional: Add messagingServiceSid if you prefer using that
    // private String messagingServiceSid;
}
