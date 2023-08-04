package com.example.Bankregistration.JWT;

import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public interface JwtGeneratorInterface {

    HashMap<String,String> generateToken(AdminRequest adminRequest);

    String generateToken(UserLoginRequest loginRequest);
}
