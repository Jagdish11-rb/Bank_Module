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
    private String mobile_number;
    private String account_number;
    private String account_type;
    private String account_ifsc;
    private String bank_name;
    private String vpa;
}
