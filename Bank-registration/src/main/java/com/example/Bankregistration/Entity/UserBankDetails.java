package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(schema = "user_bank_details")
public class UserBankDetails {
    @Id
    private String user_id;
    private String user_name;
    private String password;
    private String api_user_id;
    private String api_user_name;
    private String admin_name;
    private String admin_id;
    private String mobileNumber;
    private String accountNumber;
    private String accountType;
    private String accountIfsc;
    private String bankName;
    private String vpa;
    private String aadhaarNumber;
    private boolean isKycDone;
}
