package com.example.Bankregistration.Model.Response;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBankAccountResponse {
    private String bankId;
    private String message;
}
