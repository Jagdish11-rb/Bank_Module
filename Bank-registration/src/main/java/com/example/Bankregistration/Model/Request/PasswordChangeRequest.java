package com.example.Bankregistration.Model.Request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String id;
    private String newPassword;
    private String oldPassword;
}
