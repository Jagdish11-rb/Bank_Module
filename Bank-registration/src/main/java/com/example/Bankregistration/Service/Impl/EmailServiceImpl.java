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
    public void sendForgotPasswordOtpEmail(UserProperties properties,String otp) {
        SimpleMailMessage message =  new SimpleMailMessage();
        message.setTo(properties.getEmail());
        message.setSubject("FORGOT-PASSWORD-OTP");
        message.setText("Hello "+properties.getUser_name()+",\n      " +
                "Your forgot password otp is : "+otp+". " +
                "It will expire after 5 minutes.");
        mailSender.send(message);
    }

    @Override
    public void sendAfterRegisterEmail(UserProperties properties) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(properties.getEmail());
        message.setSubject("Welcome to ApnaBank");
        message.setText("Dear "+properties.getUser_name()+",\n"+"Welcome to ApnaBank! We are delighted to have you onboard.\n\n"
                         +"With ApnaBank, you can effortlessly manage your bank accounts,you can send and receive money, pay bills,"+
                         "and make transactions effortlessly using UPI. It's fast, secure, and designed for your convenience.\n\n"
                        +"Your unique user_id is : "+properties.getUser_id()+".\n\n"
                        +"Log in now using your unique user_id and password and start your journey towards financial success.\n\n"
                        +"If you need any assistance,out support team is here to help."+" i.jagdish4829@gmail.com\n\n"
                        +"Thank you for choosing ApnaBank\n\n"
                        +"With appretiation,\nApnaBank");
        mailSender.send(message);
    }
}
