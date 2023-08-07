package com.example.Bankregistration.JWT;

import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public interface JwtGeneratorInterface {


    String generateToken(LoginRequest loginRequest);
}
