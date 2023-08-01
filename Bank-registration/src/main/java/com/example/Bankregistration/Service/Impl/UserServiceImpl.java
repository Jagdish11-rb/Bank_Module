package com.example.Bankregistration.Service.Impl;


import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserBankDetails;
import com.example.Bankregistration.Exception.InvalidCredentialsException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Repository.UserRepository;
import com.example.Bankregistration.Service.ApiService;
import com.example.Bankregistration.Service.BackGroundService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private BackGroundService backGroundService;

    @Autowired
    private JwtGenerator jwtGenerator;
    @Override
    public boolean validateApiUser(String apiUserName) {
        ApiPartner apiPartner = apiService.findApiUserByName(apiUserName);
        if(apiPartner==null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public ApiPartner getApiUserInfo(String apiUserName) {
        ApiPartner apiPartner = apiService.findApiUserByName(apiUserName);
        return apiPartner;
    }

    @Override
    public HashMap<Integer, String> onboardUser(UserRequest userRequest, ApiPartner apiPartner) {
        log.info("Request received to onbaord user : "+userRequest);
        UserBankDetails userEntity = userRepository.findByMobileNumber(userRequest.getMobileNumber()).orElse(null);
        HashMap<Integer,String> map = new HashMap<>();
        if(userEntity!=null){
            map.put(1,null);
        }else {
            map.clear();

            UserBankDetails userBankDetails = new UserBankDetails();
            userBankDetails.setUser_id(backGroundService.generateId(userRequest));
            userBankDetails.setApi_user_id(apiPartner.getId());
            userBankDetails.setPassword(userRequest.getPassword());
            userBankDetails.setApi_user_name(apiPartner.getUsername());
            userBankDetails.setAdmin_id(apiPartner.getAdminId());
            userBankDetails.setAdmin_name(apiPartner.getAdminName());
            userBankDetails.setUser_name(userRequest.getName());
            userBankDetails.setMobileNumber(userRequest.getMobileNumber());
            userBankDetails.setAccountNumber(userRequest.getAccountNumber());
            userBankDetails.setAccountType(userRequest.getAccountType());
            userBankDetails.setAccountIfsc(userRequest.getAccountIfsc());
            userBankDetails.setBankName(userRequest.getBankName());
            userBankDetails.setVpa(userRequest.getVpa());
            userBankDetails.setKycDone(false);

            userRepository.save(userBankDetails);
            map.put(0,userBankDetails.getUser_id());
        }
        return map;
    }

    @Override
    public List<String> findAllOnboardedUsers() {
        return userRepository.findAllOnboardedUsers();
    }

    @Override
    public String getApiUser() {
        List<String> apiUserList = apiService.retreiveApiPartners();
        Random random = new Random();
        int index = random.nextInt(apiUserList.size());
        String api_user_name = apiUserList.get(index);
        ApiPartner apiPartner = apiService.findApiUserByName(api_user_name);
        if(apiPartner.isActive()==false){
            return getApiUser();
        }else{
            return api_user_name;
        }
    }

    @Override
    public UserBankDetails authenticateRequest(UserLoginRequest loginRequest) {
        UserBankDetails user = userRepository.findById(loginRequest.getId()).orElse(null);
        if(user!=null){
            boolean res = user.getPassword().matches(loginRequest.getPassword());
            if(res==true){
                return user;
            }else{
                throw new InvalidCredentialsException("Incorrect password.");
            }
        }else{
            return user;
        }
    }

    @Override
    public Map<String, String> generateToken(UserLoginRequest loginRequest) {
        return jwtGenerator.generateToken(loginRequest);
    }

    @Override
    public UserBankDetails getUserDetails(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserBankDetails findUserByMobNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber).orElse(null);
    }

    @Override
    public UserBankDetails saveUser(UserBankDetails user) {
        return userRepository.save(user);
    }

    @Override
    public boolean validateToken(HttpServletRequest httpServletRequest) {
        String token = jwtGenerator.getTokenFromAuthorization(httpServletRequest);
        return jwtGenerator.validateToken(token);
    }

    @Override
    public Claims getDataFromToken(HttpServletRequest httpServletRequest) {
        String token = jwtGenerator.getTokenFromAuthorization(httpServletRequest);
        return jwtGenerator.getDataFromToken(token);
    }

    @Override
    public UserBankDetails findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

}
