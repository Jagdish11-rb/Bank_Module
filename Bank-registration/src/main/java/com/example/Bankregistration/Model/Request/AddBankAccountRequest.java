package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBankAccountRequest {
    @NotBlank(message="Kindly provide your userId")
    private String userId;
    @NotBlank(message="Kindly provide your account number.")
    private String accountNumber;
    @NotBlank(message="Kindly provide your account type.")
    @Pattern(regexp = "^(SAVINGS|CURRENT)$",message="Invalid account type")
    private String accountType;
    @NotBlank(message="Kindly provide your account ifsc.")
    private String accountIfsc;
    @NotBlank(message="Kindly provide your bank name.")
    private String bankName;
}
