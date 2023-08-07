package com.example.Bankregistration.Controller;


import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.UserNotFoundException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Pojo.PasswordChangeRequest;
import com.example.Bankregistration.Pojo.RenewPassword;
import com.example.Bankregistration.Service.BackGroundService;
import com.example.Bankregistration.Service.EmailService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@Slf4j
public class UserUpdateController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private BackGroundService backGroundService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/user/update-user-password-using-oldpassword")
    public ResponseEntity<?> updateUser(@RequestBody PasswordChangeRequest request, @CookieValue(value="user_cookie",required = false) String cookie){
        try{
            String userId = jwtGenerator.getDataFromToken(cookie).getId();
            UserProperties user = userService.getproperties(userId);
                if(user!=null){
                    boolean res = user.getPassword().matches(request.getOldPassword());
                    if(res!=true){
                        return new ResponseEntity<>("Incorrect old password",HttpStatus.NOT_ACCEPTABLE);
                    }else{
                        if(!request.getOldPassword().matches(request.getNewPassword())){
                            user.setPassword(request.getNewPassword());
                            userService.saveUser(user);
                            return new ResponseEntity<>("Password changed.",HttpStatus.ACCEPTED);
                        }else{
                            return new ResponseEntity<>("Old password and new password can't be same.", HttpStatus.NOT_ACCEPTABLE);
                        }
                    }
                }else{
                    return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
                }
        } catch(Exception e){
            return new ResponseEntity<>("Exception occured while updating user. Reason >>>>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/send-forgot-password-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(@CookieValue(value="user_cookie",required = false) String cookie) {
        try {
            String user_id = jwtGenerator.getDataFromToken(cookie).getId();
            UserProperties properties = userService.findUserById(user_id);
            if(properties!=null){
                String otp = userService.getOtpForForgotPassword(properties);
                emailService.sendForgotPasswordOtpEmail(properties,otp);
                return new ResponseEntity<>("Forgot password otp sent to your email successfully.",HttpStatus.OK);
            }else{
                throw new UserNotFoundException("User not found.");
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while sending forgot password otp. Reason : "+e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/verify-forgot-password-otp")
    public ResponseEntity<?> verifyForgotPasswordOtp(@RequestBody String otp,@CookieValue(value="user_cookie",required = false) String cookie){
        try{
            String trimmedOtp = otp.trim();
            String user_id = jwtGenerator.getDataFromToken(cookie).getId();
            HashMap<Integer,String> map= userService.validateOtp(trimmedOtp,user_id);
            if(map.containsKey(0)){
                return new ResponseEntity<>(map.get(0),HttpStatus.ACCEPTED);
            } else if(map.containsKey(1)){
                return new ResponseEntity<>(map.get(1),HttpStatus.REQUEST_TIMEOUT);
            } else{
                return new ResponseEntity<>(map.get(-1),HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured.  Reason :"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/change-password")
    public ResponseEntity<?> changePassword(@RequestBody RenewPassword renewPassword,@CookieValue(value="user_cookie",required = false) String cookie){
        try{
            String user_id = jwtGenerator.getDataFromToken(cookie).getId();
            UserProperties userProperties = userService.findUserById(user_id);
            if(userProperties!=null){
                if(renewPassword.getPassword().matches(renewPassword.getConfirmPassword())){
                    userProperties.setPassword(renewPassword.getPassword());
                    userService.saveUser(userProperties);
                    return new ResponseEntity<>("Password changed successfully",HttpStatus.ACCEPTED);
                }else{
                    return new ResponseEntity<>("Password mismatch.",HttpStatus.NOT_ACCEPTABLE);
                }
            }else{
                return new ResponseEntity<>("user not found",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured.  Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

}
