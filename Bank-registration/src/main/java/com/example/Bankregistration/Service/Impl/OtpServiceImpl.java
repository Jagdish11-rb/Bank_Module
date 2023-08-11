package com.example.Bankregistration.Service.Impl;


import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.BankDetailsValidationException;
import com.example.Bankregistration.Exception.DuplicateBankAccountException;
import com.example.Bankregistration.Exception.InvalidCredentialsException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.AddBankAccountRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Repository.OtpRepository;
import com.example.Bankregistration.Repository.UserBankRepository;
import com.example.Bankregistration.Repository.UserRepository;
import com.example.Bankregistration.Service.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    @Autowired
    private BackGroundService backGroundService;

    @Autowired
    private OtpRepository otpRepository;


    @Override
    public String getOtpForForgotPassword(UserProperties properties) {
        ForgotPasswordOtpProperties forgotPasswordOtpEntity = new ForgotPasswordOtpProperties();

        Optional<ForgotPasswordOtpProperties> previousOtp = otpRepository.findById(properties.getUser_id());
        if(!previousOtp.isEmpty()){
            otpRepository.deleteById(properties.getUser_id());
        }

        forgotPasswordOtpEntity.setId(properties.getUser_id());
        forgotPasswordOtpEntity.setOtp(backGroundService.generateOtp());
        forgotPasswordOtpEntity.setGeneratedTime(LocalDateTime.now());
        forgotPasswordOtpEntity.setExpiry(forgotPasswordOtpEntity.getGeneratedTime().plusMinutes(10));
        log.info(String.valueOf(forgotPasswordOtpEntity));
        otpRepository.save(forgotPasswordOtpEntity);

        return forgotPasswordOtpEntity.getOtp();
    }

    @Override
    public void save(ForgotPasswordOtpProperties otp) {
        otpRepository.save(otp);
    }

    @Override
    public ForgotPasswordOtpProperties findOtpDetailsById(String userId) {
        return otpRepository.findById(userId).orElse(null);
    }

    @Override
    public HashMap<Integer,String> validateOtp(String otp,String user_id) {
        HashMap<Integer, String> map = new HashMap<>();
        ForgotPasswordOtpProperties otpProperties = otpRepository.findById(user_id).orElse(null);
        if(otpProperties!=null){
            if(!(otpProperties.getOtp().matches(otp))){
                map.clear();
                map.put(-1,"Incorrect OTP.");
            }else{
                map.clear();
                if(otpProperties.getExpiry().isBefore(LocalDateTime.now())){
                }else{
                    otpProperties.setOtpVerified(true);
                    otpRepository.save(otpProperties);
                    map.put(0,"OTP verified.");
                }
            }
        }else{
            map.put(-1,"Invalid credentials.");
        }
        return map;
    }
}
