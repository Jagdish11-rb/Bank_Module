package com.example.Bankregistration.Controller;

import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserBankDetails;
import com.example.Bankregistration.Model.Request.PasswordChangeRequest;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Model.Response.UserResponse;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/onboard-user")
    public ResponseEntity<?> onboardUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse response = new UserResponse();
        String api_user_name = userService.getApiUser();
        try{
            ApiPartner apiPartner = userService.getApiUserInfo(api_user_name);
            HashMap<Integer,String> map = userService.onboardUser(userRequest,apiPartner);
            if(map.containsKey(0)){
                response.setId(map.get(0));
                response.setName(userRequest.getName());
                response.setApi_user_name(api_user_name);
                response.setResponse("User onboarded successfully.");
                return new ResponseEntity<>(response,HttpStatus.CREATED);
            }else{
                response.setId(null);
                response.setName(userRequest.getName());
                response.setResponse("User already present with this mobile number. Please try again with another mobile number.");
                return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            response.setId(null);
            response.setName(userRequest.getName());
            response.setResponse("Exception occured while onboarding user.  Reason >>>>>>>>>"+e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/get-all-onboarded-users")
    public ResponseEntity<?> retrieveAllUsers(){
        try{
            List<String> list = userService.findAllOnboardedUsers();
            return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching onboarded users."+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/user-login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest loginRequest){
        try{
            UserBankDetails user = userService.authenticateRequest(loginRequest);
            if(user!=null){
                Map<String,String> token = userService.generateToken(loginRequest);
                return new ResponseEntity<>(token,HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while logging in user. Reason >>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/fetch-details")
    public ResponseEntity<?> fetchDetails(@RequestBody String user_id){
        try{
            UserBankDetails user = userService.getUserDetails(user_id);
            if(user!=null){
                return new ResponseEntity<>(user,HttpStatus.FOUND);
            }else{
                return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching details of user.  Reason >>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/user/update-user-profile")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, HttpServletRequest httpServletRequest) {
        try {
            boolean isValid = userService.validateToken(httpServletRequest);
            if(isValid!=true){
                return new ResponseEntity<>("Invalid token.",HttpStatus.NOT_ACCEPTABLE);
            }else{
                UserBankDetails user = userService.findUserByMobNumber(userRequest.getMobileNumber());
                if (user != null) {
                    user.setVpa(userRequest.getVpa());
                    user.setUser_name(userRequest.getName());
                    user.setAccountNumber(userRequest.getAccountNumber());
                    user.setAccountIfsc(userRequest.getAccountIfsc());
                    user.setAccountType(userRequest.getAccountType());
                    user.setMobileNumber(userRequest.getMobileNumber());
                    user.setBankName(userRequest.getBankName());

                    userService.saveUser(user);
                    return new ResponseEntity<>("User updated successfully.Password and mobile number can't be changed in profile update.", HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
                }
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while updating user. Reason >>>>>>>>" + e.getMessage(), HttpStatus.CONFLICT);
        }

    }
    @PutMapping("/user/update-user-password")
    public ResponseEntity<?> updateUser(@RequestBody PasswordChangeRequest request, HttpServletRequest httpServletRequest){
        try{
            boolean isValid = userService.validateToken(httpServletRequest);
            if(isValid!=true){
                return new ResponseEntity<>("Invalid token.",HttpStatus.NOT_ACCEPTABLE);
            }else{
                UserBankDetails user = userService.getUserDetails(request.getId());
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


    @PostMapping("/user/do-kyc-verification")
    public ResponseEntity<?> doKyc(@RequestBody  String aadhaarNumber ,HttpServletRequest httpServletRequest) {
        try {
            boolean isValid = userService.validateToken(httpServletRequest);
            if (isValid != true) {
                return new ResponseEntity<>("Invalid token.", HttpStatus.NOT_ACCEPTABLE);
            } else {
                Claims claims = userService.getDataFromToken(httpServletRequest);
                UserBankDetails user = userService.findUserById(claims.getId());
                if(user.isKycDone()==true){
                    return new ResponseEntity<>("KYC verification already done.",HttpStatus.ALREADY_REPORTED);
                }
                if (user != null) {
                    if (user.getPassword().matches(claims.getSubject())) {
                        if (aadhaarNumber.length() == 12) {
                            user.setAadhaarNumber(aadhaarNumber);
                            user.setKycDone(true);
                            userService.saveUser(user);
                            return new ResponseEntity<>("KYC verification successful.",HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>("KYC verification failed.", HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Incorrect token details.", HttpStatus.NO_CONTENT);
                    }
                } else {
                    return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while updating user. Reason >>>>>>>>" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
