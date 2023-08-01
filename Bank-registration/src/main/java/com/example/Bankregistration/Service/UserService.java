package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserBankDetails;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {

    boolean validateApiUser(String apiUserName);

    ApiPartner getApiUserInfo(String apiUserName);

    HashMap<Integer, String> onboardUser(UserRequest userRequest, ApiPartner apiPartner);

    List<String> findAllOnboardedUsers();

    String getApiUser();

    UserBankDetails authenticateRequest(UserLoginRequest loginRequest);

    Map<String, String> generateToken(UserLoginRequest loginRequest);

    UserBankDetails getUserDetails(String apiUserId);
    
    UserBankDetails findUserByMobNumber(String mobileNumber);

    UserBankDetails saveUser(UserBankDetails user);

    boolean validateToken(HttpServletRequest httpServletRequest);

    Claims getDataFromToken(HttpServletRequest httpServletRequest);

    UserBankDetails findUserById(String id);
}
