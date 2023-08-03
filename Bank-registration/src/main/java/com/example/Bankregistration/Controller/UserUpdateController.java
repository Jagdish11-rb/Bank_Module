package com.example.Bankregistration.Controller;


import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.UserNotFoundException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Pojo.CustomClaims;
import com.example.Bankregistration.Pojo.PasswordChangeRequest;
import com.example.Bankregistration.Service.BackGroundService;
import com.example.Bankregistration.Service.EmailService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
    public ResponseEntity<?> updateUser(@RequestBody PasswordChangeRequest request, HttpServletRequest httpServletRequest){
        try{
            String token = jwtGenerator.getTokenFromAuthorization(httpServletRequest);
            boolean isValid = jwtGenerator.validateToken(token);
            if(isValid!=true){
                return new ResponseEntity<>("Invalid token.",HttpStatus.NOT_ACCEPTABLE);
            }else{
                UserProperties user = userService.getproperties(request.getId());
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
            }

        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while updating user. Reason >>>>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/send-forgot-password-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(@RequestBody String user_id) {
        try {
                UserProperties properties = userService.findUserById(user_id);
                if(properties!=null){
                    String otp = userService.getOtpForForgotPassword(properties);
                    emailService.sendSimpleEmail(properties,otp);
                    return new ResponseEntity<>("Forgot password otp sent to your email successfully.",HttpStatus.OK);
                }else{
                    throw new UserNotFoundException("User not found.");
                }
        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while sending forgot password otp. Reason : "+e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/verify-forgot-password-otp")
    public ResponseEntity<?> changePasswordUsingOtp(@RequestBody String otp,@RequestHeader("user_id") String user_id){
        try{
            HashMap<Integer,String> map= userService.validateOtp(otp,user_id);
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
}
