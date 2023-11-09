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

public interface UserService {

    boolean validateApiUser(String apiUserName);

    ApiPartner getApiUserInfo(String apiUserName);

    HashMap<Boolean, String> onboardUser(UserRequest userRequest, ApiPartner apiPartner);

    List<String> findAllOnboardedUsers();

    ApiPartner getApiUser();

    UserProperties authenticateUser(LoginRequest loginRequest);


    UserProperties getproperties(String apiUserId);
    
    UserProperties findUserByMobNumber(String mobileNumber);

    UserProperties saveUser(UserProperties user);


    UserProperties findUserById(String id);

    UserProperties findUserByEmail(String email);

    void validateBankDetails(AddBankAccountRequest request);

    String getUserInfoFromCookies(HttpServletRequest request);

    UserBankProperties prepareBankDetails(UserProperties user, AddBankAccountRequest bankRequest);

    void removeUser(UserProperties user);

    UserBankProperties findBankAccountById(String id);

    void removeBankAccount(String bankId);

    void checkForDuplicateBankAccount(AddBankAccountRequest bankRequest);

}
