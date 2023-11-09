package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Model.Request.AddBankAccountRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;

public interface OtpService {

    ForgotPasswordOtpProperties findOtpDetailsById(String userId);

    HashMap<Boolean,String> validateOtp(String otp,String user_id);

    String getOtpForForgotPassword(UserProperties properties);

    void save(ForgotPasswordOtpProperties otp);
}
