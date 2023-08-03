package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleEmail(UserProperties properties,String otp) {
        SimpleMailMessage message =  new SimpleMailMessage();
        message.setTo(properties.getEmail());
        message.setSubject("FORGOT-PASSWORD-OTP");
        message.setText("Hello "+properties.getUser_name()+",\n      " +
                "Your forgot password otp is : "+otp+". " +
                "It will expire after 5 minutes.");
        mailSender.send(message);
    }
}
