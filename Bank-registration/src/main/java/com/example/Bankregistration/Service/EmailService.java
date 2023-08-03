package com.example.Bankregistration.Service;


import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Model.Request.UserRequest;

public interface EmailService {

    void sendSimpleEmail(UserProperties properties,String otp);
}
