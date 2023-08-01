package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminRequest {
    @Size(min=5,max=5,message = "Id should be of 5 digits.")
    private String id;
    private String user_name;
    private String password;
}
