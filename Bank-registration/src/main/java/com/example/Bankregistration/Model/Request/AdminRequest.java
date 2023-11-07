package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminRequest {
    @Size(min=5,max=5,message = "Id should be of 5 digits.")
    @NotBlank(message="Id should not be blank.")
    @NotNull(message="Id field should not be null.")
    @Pattern(message = "Invalid input amount", regexp = "[0-9.]+")
    private String id;
    @NotBlank(message="Username should not be blank.")
    private String user_name;
    @NotBlank(message="Password should not be blank.")
    private String password;
}
