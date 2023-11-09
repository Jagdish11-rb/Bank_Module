package com.example.Bankregistration.Model.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message="Kindly provide a username.")
    private String name;
    @NotBlank(message="Kindly provide a password")
    private String password;
    @NotBlank(message="Kindly provide a mobile number")
    @Pattern(regexp = "[0-9\\\\d]{10}",message="Kindly provide valid mobile number")
    private String mobileNumber;
    private String state;
    private String city;
    @NotBlank(message="Kindly provide a pin code")
    @Size(min=6,max=6,message="Kindly provide a valid pincode")
    private String pin_code;
    private String pan_number;
    @NotBlank(message="Kindly provide an email")
    @Email(message="Kindly provide a valid email")
    private String email;
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Invalid DOB format.Reference : DOB : '11-07-2001'")
    private String DOB;
}
