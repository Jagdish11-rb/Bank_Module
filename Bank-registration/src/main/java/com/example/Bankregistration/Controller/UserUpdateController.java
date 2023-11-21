package com.example.Bankregistration.Controller;


import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.UserNotFoundException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Pojo.PasswordChangeRequest;
import com.example.Bankregistration.Pojo.RenewPassword;
import com.example.Bankregistration.Service.BackGroundService;
import com.example.Bankregistration.Service.EmailService;
import com.example.Bankregistration.Service.OtpService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/user/update-user-password-using-oldpassword")
    public ResponseEntity<?> updatePasswordUsingOldPassword(@RequestBody PasswordChangeRequest request, @CookieValue(value="user_cookie",required = false) String cookie){
        try{
            String userId = jwtGenerator.getDataFromToken(cookie).getId();
            UserProperties user = userService.getproperties(userId);
                if(user!=null){
                    if(!(user.getPassword().matches(request.getOldPassword()))){
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
    public ResponseEntity<?> sendForgotPasswordOtp(@RequestHeader("user_id") String user_id) {
        try {
            UserProperties properties = userService.findUserById(user_id);
            if(properties!=null){
                String otp = otpService.getOtpForForgotPassword(properties);
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
    public ResponseEntity<?> verifyForgotPasswordOtp(@RequestBody String otp,@RequestHeader("user_id") String user_id){
        try{
            String trimmedOtp = otp.trim();
            HashMap<Boolean,String> map= otpService.validateOtp(trimmedOtp,user_id);
            if(map.containsKey(true)){
                return new ResponseEntity<>(map.get(true),HttpStatus.ACCEPTED);
            } else{
                return new ResponseEntity<>(map.get(false),HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured.  Reason :"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody RenewPassword renewPassword, BindingResult result, @RequestHeader("user_id") String user_id){
        try{

            if(result.hasErrors()){
                return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.BAD_REQUEST);
            }
            UserProperties userProperties = userService.findUserById(user_id);
            ForgotPasswordOtpProperties otp = otpService.findOtpDetailsById(user_id);
                if(userProperties!=null && otp!=null){
                    if(otp.isOtpVerified()){
                        if(otp.isPasswordChanged()!=true){
                            if(renewPassword.getPassword().matches(renewPassword.getConfirmPassword())){
                                userProperties.setPassword(renewPassword.getPassword());
                                otp.setPasswordChanged(true);
                                otpService.save(otp);
                                userService.saveUser(userProperties);
                                return new ResponseEntity<>("Password changed successfully",HttpStatus.ACCEPTED);
                            }else{
                                return new ResponseEntity<>("Password mismatch.",HttpStatus.NOT_ACCEPTABLE);
                            }
                        }else{
                            return new ResponseEntity<>("Password already changed.",HttpStatus.UNAUTHORIZED);
                        }
                    }else{
                        return new ResponseEntity<>("Otp is not verified.",HttpStatus.SERVICE_UNAVAILABLE);
                    }
                }else{
                    return new ResponseEntity<>("user or otp details not found",HttpStatus.NOT_FOUND);
                }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured.  Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

}
