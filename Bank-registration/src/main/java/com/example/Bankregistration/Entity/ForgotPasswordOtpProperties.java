package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ForgotPasswordOtpProperties {

    @Id
    private String Id;
    private String otp;
    private boolean isOtpVerified;
    private boolean isPasswordChanged;
    private LocalDateTime generatedTime;
    private LocalDateTime expiry;
}
