package com.example.Bankregistration.Pojo;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String newPassword;
    private String oldPassword;
}
