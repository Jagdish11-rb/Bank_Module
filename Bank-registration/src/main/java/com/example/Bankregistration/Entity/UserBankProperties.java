package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UserBankProperties {
    @Id
    private String bankId;
    private String userId;
    private String mobileNumber;
    private String accountNumber;
    private String accountType;
    private String accountIfsc;
    private String bankName;
    private String vpa;
}
