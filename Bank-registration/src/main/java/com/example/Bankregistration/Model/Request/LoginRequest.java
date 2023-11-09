package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message="Please provide id.")
    private String id;
    @NotBlank(message="Please provide password.")
    private String password;
}
