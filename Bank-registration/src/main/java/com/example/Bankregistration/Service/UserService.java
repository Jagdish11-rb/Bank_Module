package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {

    boolean validateApiUser(String apiUserName);

    ApiPartner getApiUserInfo(String apiUserName);

    HashMap<Integer, String> onboardUser(UserRequest userRequest, ApiPartner apiPartner);

    List<String> findAllOnboardedUsers();

    UserBankProperties prepareBankDetails(UserRequest userRequest, UserProperties properties);

    String getApiUser();

    UserProperties authenticateRequest(UserLoginRequest loginRequest);


    UserProperties getproperties(String apiUserId);
    
    UserProperties findUserByMobNumber(String mobileNumber);

    UserProperties saveUser(UserProperties user);


    UserProperties findUserById(String id);

    UserProperties findUserByEmail(String email);

    void validateBankDetails(UserRequest userRequest);

    String getOtpForForgotPassword(UserProperties properties);

    HashMap<Integer,String> validateOtp(String otp,String user_id);

    String getUserInfoFromCookies(HttpServletRequest request);
}
