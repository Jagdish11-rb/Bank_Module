package com.example.Bankregistration.Service.Impl;


import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.BankDetailsValidationException;
import com.example.Bankregistration.Exception.InvalidCredentialsException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Repository.OtpRepository;
import com.example.Bankregistration.Repository.UserBankRepository;
import com.example.Bankregistration.Repository.UserRepository;
import com.example.Bankregistration.Service.ApiService;
import com.example.Bankregistration.Service.BackGroundService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBankRepository bankRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private BackGroundService backGroundService;

    @Autowired
    private OtpRepository otpRepository;

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
        UserProperties userEntity = userRepository.findByMobileNumber(userRequest.getMobileNumber()).orElse(null);
        HashMap<Integer,String> map = new HashMap<>();
        if(userEntity!=null){
            map.put(1,null);
        }else {
            map.clear();
            UserProperties properties = new UserProperties();
            properties.setUser_id(backGroundService.generateUserId(userRequest));
            properties.setApi_user_id(apiPartner.getId());
            properties.setPassword(userRequest.getPassword());
            properties.setApi_user_name(apiPartner.getUsername());
            properties.setAdmin_id(apiPartner.getAdminId());
            properties.setAdmin_name(apiPartner.getAdminName());
            properties.setUser_name(userRequest.getName());
            properties.setMobileNumber(userRequest.getMobileNumber());
            properties.setCreated_date(LocalDateTime.now().toString());
            properties.setPanNumber(userRequest.getPan_number());
            properties.setState(userRequest.getState());
            properties.setCity(userRequest.getCity());
            properties.setPin_code(userRequest.getPin_code());
            properties.setEmail(userRequest.getEmail());
            properties.setDOB(userRequest.getDOB());
            properties.setKycDone(false);

            UserBankProperties bankProperties = prepareBankDetails(userRequest,properties);

            bankRepository.save(bankProperties);
            userRepository.save(properties);

            map.put(0,properties.getUser_id());
        }
        return map;
    }

    @Override
    public List<String> findAllOnboardedUsers() {
        return userRepository.findAllOnboardedUsers();
    }

    @Override
    public UserBankProperties prepareBankDetails(UserRequest userRequest, UserProperties properties) {

        UserBankProperties bankProperties = new UserBankProperties();

        bankProperties.setUser_id(properties.getUser_id());
        bankProperties.setApi_user_id(properties.getApi_user_id());
        bankProperties.setApi_user_name(properties.getApi_user_name());
        bankProperties.setAdmin_id(properties.getAdmin_id());
        bankProperties.setAdmin_name(properties.getAdmin_name());
        bankProperties.setAccount_ifsc(userRequest.getAccountIfsc());
        bankProperties.setAccount_number(userRequest.getAccountNumber());
        bankProperties.setBank_name(userRequest.getBankName());
        bankProperties.setMobile_number(userRequest.getMobileNumber());
        bankProperties.setAccount_type(userRequest.getAccountType());
        bankProperties.setVpa(userRequest.getVpa());

        return bankProperties;
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
    public UserProperties authenticateRequest(UserLoginRequest loginRequest) {
        UserProperties user = userRepository.findById(loginRequest.getId()).orElse(null);
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
    public UserProperties getproperties(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserProperties findUserByMobNumber(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber).orElse(null);
    }

    @Override
    public UserProperties saveUser(UserProperties user) {
        return userRepository.save(user);
    }

    @Override
    public UserProperties findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserProperties findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void validateBankDetails(UserRequest userRequest) {
        if(userRequest.getAccountNumber().isEmpty()){
            throw new BankDetailsValidationException("Please provide account number.Can't be empty.");
        }
        if(userRequest.getAccountType().isEmpty()) {
            throw new BankDetailsValidationException("Please provide account type.Can't be empty.");
        }
        if(userRequest.getAccountIfsc().isEmpty()) {
            throw new BankDetailsValidationException("Please provide account ifsc code.Can't be empty.");
        }

        if(userRequest.getVpa().isEmpty()) {
            throw new BankDetailsValidationException("Please provide virtual address.Can't be empty.");
        }
        if(userRequest.getBankName().isEmpty()) {
            throw new BankDetailsValidationException("Please provide bank name.Can't be empty.");
        }


    }

    @Override
    public String getOtpForForgotPassword(UserProperties properties) {
        ForgotPasswordOtpProperties forgotPasswordOtpEntity = new ForgotPasswordOtpProperties();

        Optional<ForgotPasswordOtpProperties> previousOtp = otpRepository.findById(properties.getUser_id());
        if(!previousOtp.isEmpty()){
            otpRepository.deleteById(properties.getUser_id());
        }

        forgotPasswordOtpEntity.setId(properties.getUser_id());
        forgotPasswordOtpEntity.setOtp(backGroundService.generateOtp());
        forgotPasswordOtpEntity.setGeneratedTime(LocalDateTime.now());
        forgotPasswordOtpEntity.setExpiry(forgotPasswordOtpEntity.getGeneratedTime().plusMinutes(60));
        log.info(String.valueOf(forgotPasswordOtpEntity));
        otpRepository.save(forgotPasswordOtpEntity);

        return forgotPasswordOtpEntity.getOtp();
    }

    @Override
    public HashMap<Integer,String> validateOtp(String otp,String user_id) {
        HashMap<Integer, String> map = new HashMap<>();
        ForgotPasswordOtpProperties otpProperties = otpRepository.findById(user_id).orElse(null);
        if(otpProperties!=null){
            if(!(otpProperties.getOtp().matches(otp))){
                map.clear();
                map.put(-1,"Incorrect OTP.");
            }else{
                map.clear();
                if(otpProperties.getExpiry().isBefore(LocalDateTime.now())){
                    map.put(1,"OTP Expired.");
                }else{
                    map.put(0,"OTP verified.");
                }
            }
        }else{
            map.put(-1,"Invalid credentials.");
        }
        return map;
    }

    @Override
    public String getUserInfoFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String token = cookie.getValue();
            boolean isValid = jwtGenerator.validateToken(token);
            if (isValid == true) {
                Claims claims = jwtGenerator.getDataFromToken(token);
                return claims.getId();
            }
        }
        return null;
    }
}
