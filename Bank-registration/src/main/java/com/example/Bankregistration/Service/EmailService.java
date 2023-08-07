package com.example.Bankregistration.Service;


import com.example.Bankregistration.Entity.UserProperties;

public interface EmailService {

    void sendForgotPasswordOtpEmail(UserProperties properties,String otp);

    void sendAfterRegisterEmail(UserProperties properties);
}
