package com.example.Bankregistration.Service;


import com.example.Bankregistration.Entity.UserProperties;

public interface EmailService {

    void sendSimpleEmail(UserProperties properties,String otp);
}
