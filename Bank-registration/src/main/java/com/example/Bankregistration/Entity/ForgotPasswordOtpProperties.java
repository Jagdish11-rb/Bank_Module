package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ForgotPasswordOtpProperties {

    @Id
    private String uniqueId;
    private String user_id;
    private String otp;
    private LocalDateTime generatedTime;
    private LocalDateTime expiry;
}
