package com.example.Bankregistration.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class UserProperties {
    @Id
    private String user_id;
    private String user_name;
    private String password;
    private String api_user_id;
    private String api_user_name;
    private String admin_name;
    private String admin_id;
    private String mobileNumber;
    private int bankAccounts;
    private String aadhaarNumber;
    private String panNumber;
    private String state;
    private String city;
    private String pin_code;
    private String created_date;
    private String email;
    private String DOB;
    private boolean isKycDone;
}
