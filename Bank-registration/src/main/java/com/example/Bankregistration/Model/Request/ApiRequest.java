package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiRequest {
    @NotBlank(message="Please provide a user_id.")
    private String api_user_id;
    @NotBlank(message="Please provide a user_name.")
    private String api_user_name;
}
