package com.example.Bankregistration.Service;


import com.example.Bankregistration.Model.Request.UserRequest;

public interface BackGroundService {

    String generateUserId(UserRequest userRequest);

    String generateOtp();

    String generateBankId();
}
