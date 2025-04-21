package com.prajaavaani.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message; // e.g., "Authentication successful"
    private UUID userId;
    private String mobileNumber;
    // Include the JWT token for session management
    private String token; 
}
