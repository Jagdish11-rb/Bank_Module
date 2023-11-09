package com.example.Bankregistration.Pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenewPassword {
    @NotBlank(message="Can't be empty")
    private String password;
    @NotBlank(message="Can't be empty")
    private String confirmPassword;
}
