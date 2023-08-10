package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBankAccountRequest {

    private String userId;
    private String accountNumber;
    private String accountType;
    private String accountIfsc;
    private String bankName;
}
