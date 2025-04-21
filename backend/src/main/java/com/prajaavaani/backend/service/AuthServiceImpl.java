package com.prajaavaani.backend.service;

import com.prajaavaani.backend.dto.AuthRequest;
import com.prajaavaani.backend.dto.AuthResponse;
import com.prajaavaani.backend.dto.VerifyOtpRequest;
import com.prajaavaani.backend.model.UserEntity;
import com.prajaavaani.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
// import org.springframework.security.authentication.BadCredentialsException; // Example specific exception

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Lombok constructor injection for final fields
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final OtpService otpService; 
    private final JwtService jwtService; // Inject JwtService

    @Override
    public void requestOtp(AuthRequest authRequest) {
        log.info("OTP requested for mobile number: {}", authRequest.getMobileNumber());
        
        // 1. Generate and store OTP using OtpService
        String otp = otpService.generateAndStoreOtp(authRequest.getMobileNumber());
        
        // 2. Send OTP via SMS service using OtpService
        try {
            otpService.sendOtpSms(authRequest.getMobileNumber(), otp);
            log.info("OTP requested and sent successfully to {}", authRequest.getMobileNumber());
        } catch (Exception e) {
            // Log the error and potentially rethrow a service-specific exception
            log.error("Failed to send OTP for mobile number {}: {}", authRequest.getMobileNumber(), e.getMessage());
            // Depending on requirements, might want to clear the stored OTP if sending failed critically
            // Or throw a custom exception like OtpSendingFailedException
            throw new RuntimeException("Failed to send OTP. Please try again later.", e); 
        }
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        log.info("Verifying OTP for mobile number: {}", verifyOtpRequest.getMobileNumber());

        // 1. Validate OTP using OtpService
        boolean isValid = otpService.validateOtp(verifyOtpRequest.getMobileNumber(), verifyOtpRequest.getOtpCode());

        if (!isValid) {
            log.warn("Invalid OTP provided for {}", verifyOtpRequest.getMobileNumber());
            // Throw an appropriate exception for invalid OTP
            // Example: throw new BadCredentialsException("Invalid OTP provided.");
            throw new RuntimeException("Invalid OTP"); // Placeholder
        }

        // OTP is valid, proceed to find or create user
        UserEntity user = userRepository.findByMobileNumber(verifyOtpRequest.getMobileNumber())
                .orElseGet(() -> {
                    // User doesn't exist, create a new one
                    log.info("Creating new user for mobile number: {}", verifyOtpRequest.getMobileNumber());
                    UserEntity newUser = new UserEntity();
                    newUser.setMobileNumber(verifyOtpRequest.getMobileNumber());
                    // Set other default fields if necessary (e.g., isVerified = true)
                    return userRepository.save(newUser);
                });
        
        log.info("OTP verified successfully for user ID: {}", user.getId());

        // Generate JWT token using UserEntity (needs UserDetails implementation)
        // For simplicity, we'll create a basic UserDetails on the fly.
        // In a real app, you'd have a UserDetailsService bean.
        org.springframework.security.core.userdetails.User userDetails = 
            new org.springframework.security.core.userdetails.User(
                user.getMobileNumber(), 
                "", // Password field is not used in OTP auth but required by UserDetails
                Collections.emptyList() // No specific authorities for this example
            );
            
        String token = jwtService.generateToken(userDetails);
        log.info("Generated JWT token for user {}", user.getMobileNumber());

        // Return successful response with the token
        AuthResponse response = new AuthResponse("Authentication successful", user.getId(), user.getMobileNumber(), token);
        return response;
    }
}
