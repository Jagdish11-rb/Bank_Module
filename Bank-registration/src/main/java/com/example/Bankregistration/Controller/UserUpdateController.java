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

//    @GetMapping("send-email-sample")
//    public ResponseEntity<?> sendEmail(@RequestBody String email){
//        emailService.sendSimpleEmail(email,"Hello","Hiii");
//        return new ResponseEntity<>("Sent successfully.", HttpStatus.ACCEPTED);
//    }

    @PostMapping("/user/send-forgot-password-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(HttpServletRequest httpServletRequest) {
        try {
            String token = jwtGenerator.getTokenFromAuthorization(httpServletRequest);
            boolean isValid = jwtGenerator.validateToken(token);
            if (isValid == true) {
                Claims claims = jwtGenerator.getDataFromToken(token);
                UserProperties properties = userService.findUserById(claims.getId());
                log.info(String.valueOf(properties));
                if(properties!=null){
                    String otp = userService.getOtpForForgotPassword(properties);
                    emailService.sendSimpleEmail(properties,otp);
                    return new ResponseEntity<>("Forgot password otp sent to your email successfully.",HttpStatus.OK);
                }else{
                    throw new UserNotFoundException("User not found.");
                }
            } else {
                return new ResponseEntity<>("Invalid token", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while sending forgot password otp. Reason : "+e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
