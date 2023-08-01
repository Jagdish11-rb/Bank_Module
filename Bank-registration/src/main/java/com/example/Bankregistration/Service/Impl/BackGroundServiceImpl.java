package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Service.BackGroundService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BackGroundServiceImpl implements BackGroundService {
    @Override
    public String generateId(UserRequest userRequest) {
        String userName = ((userRequest.getName()).substring(0,3)).toUpperCase();
        Random random = new Random();
        int num = random.nextInt(999);
        String id = userName.concat(String.valueOf(num));
        return id;
    }
}
