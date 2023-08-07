package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Repository.UserBankRepository;
import com.example.Bankregistration.Repository.UserRepository;
import com.example.Bankregistration.Service.BackGroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class BackGroundServiceImpl implements BackGroundService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBankRepository bankRepository;

    @Override
    public String generateUserId(UserRequest userRequest) {
        String user_name=userRequest.getName().trim();
        String userName = (user_name.substring(0,3)).toUpperCase();
        Random random = new Random();
        int num = random.nextInt(999);
        String id = userName.concat(String.valueOf(num));
        Optional<UserProperties> user = userRepository.findById(id);
        if(!user.isEmpty()){
            return generateUserId(userRequest);
        }
        return id;
    }

    @Override
    public String generateOtp() {
        String value = "0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<6;i++){
            int index= random.nextInt(value.length());
            char ch = value.charAt(index);
            stringBuilder.append(ch);
        }
        String otp = stringBuilder.toString();
        return otp;
    }

    @Override
    public String generateBankId() {
       String num = "1234567890";
       StringBuilder stringBuilder = new StringBuilder();
       stringBuilder.append("BNK");
       Random random = new Random();
       for(int i=0;i<8;i++){
           int index =random.nextInt(num.length());
           char ch = num.charAt(index);
           stringBuilder.append(ch);
       }
       String bankId = stringBuilder.toString();
       Optional<UserBankProperties> bankProperties = bankRepository.findById(bankId);
       if(!bankProperties.isEmpty()){
           return generateBankId();
       }
       return bankId;
    }
}
