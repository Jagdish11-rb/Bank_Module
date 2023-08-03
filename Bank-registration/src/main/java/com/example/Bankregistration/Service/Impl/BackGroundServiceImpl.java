package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Service.BackGroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BackGroundServiceImpl implements BackGroundService {

    @Override
    public String generateUserId(UserRequest userRequest) {
        String userName = ((userRequest.getName()).substring(0,3)).toUpperCase();
        Random random = new Random();
        int num = random.nextInt(999);
        String id = userName.concat(String.valueOf(num));
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
    public String generateUniqueId(String key) {
        String num = "1234567890";
        StringBuilder stringBuilder =new StringBuilder();
        Random random=new Random();
        for(int i=0;i<5;i++){
            int index = random.nextInt(num.length());
            char ch = num.charAt(index);
            stringBuilder.append(ch);
            if(i==2){
                stringBuilder.append(key.toUpperCase());
            }
        }
        String uniqueId = stringBuilder.toString();
        return uniqueId;
    }

}
