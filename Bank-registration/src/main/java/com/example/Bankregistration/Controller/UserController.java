package com.example.Bankregistration.Controller;

import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserBankProperties;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.FetchRequest;
import com.example.Bankregistration.Model.Request.AddBankAccountRequest;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import com.example.Bankregistration.Model.Request.UserRequest;
import com.example.Bankregistration.Model.Response.AddBankAccountResponse;
import com.example.Bankregistration.Model.Response.UserResponse;
import com.example.Bankregistration.Service.EmailService;
import com.example.Bankregistration.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private EmailService emailService;

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

    @PostMapping("user/add-bank-account")
    public ResponseEntity<?> addBankAccount(@RequestBody AddBankAccountRequest bankRequest , HttpServletRequest request){
        try{
            UserProperties user = userService.findUserById(userService.getUserInfoFromCookies(request));
            if(user!=null){
                if(user.getUser_id().matches(bankRequest.getUser_id())){
                    userService.validateBankDetails(bankRequest);
                    UserBankProperties userBankProperties = userService.prepareBankDetails(user,bankRequest);
                    AddBankAccountResponse response=new AddBankAccountResponse();
                    response.setBankId(userBankProperties.getBank_id());
                    response.setVpa(userBankProperties.getVpa());
                    response.setMessage("Bank account added successfully.");
                    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
                }else{
                    return new ResponseEntity<>("Something went wrong.",HttpStatus.NOT_ACCEPTABLE);
                }
            }else{
                return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while adding bank account . Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/user/get-all-onboarded-users")
    public ResponseEntity<?> retrieveAllUsers(){
        try{
            List<String> list = userService.findAllOnboardedUsers();
            return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching onboarded users."+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/user-login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest loginRequest,HttpServletResponse response){
        try{
            UserProperties user = userService.authenticateRequest(loginRequest);
            if(user!=null){
                String token = jwtGenerator.generateToken(loginRequest);
                Cookie cookie = new Cookie("myBank_cookie",token);
                cookie.setMaxAge((int) (jwtGenerator.getExpiration()/100));
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                return new ResponseEntity<>("Logged in successfully.",HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while logging in user. Reason >>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("user/user-logout")
    public ResponseEntity<?> logoutUser(@CookieValue(value="myBank_cookie",required = false) String cookie,HttpServletResponse response){
        try{
            if(cookie!=null) {
                Cookie newCookie = new Cookie("myBank_cookie", null);
                newCookie.setMaxAge(0);
                newCookie.setHttpOnly(true);
                response.addCookie(newCookie);
            }
            return new ResponseEntity<>("Logged out successfully.",HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while logging out."+e.getMessage(),HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/user/get-logged-in-user-details")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
        try{
            String userId = userService.getUserInfoFromCookies(request);
            UserProperties user = userService.findUserById(userId);
            if(user!=null){
                return new ResponseEntity<>(user,HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured. Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/user/fetch-details")
    public ResponseEntity<?> fetchDetails(@RequestBody String user_id){
        try{
            UserProperties user = userService.getproperties(user_id);
            if(user!=null){
                return new ResponseEntity<>(user,HttpStatus.FOUND);
            }else{
                return new ResponseEntity<>("User not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching details of user.  Reason >>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/user/do-kyc-verification")
    public ResponseEntity<?> doKyc(@RequestBody  String aadhaarNumber,HttpServletRequest request) {
        try {
            String userId = userService.getUserInfoFromCookies(request);
                UserProperties user = userService.findUserById(userId);
                if (user != null) {
                    if(user.isKycDone()==true){
                        return new ResponseEntity<>("KYC verification already done.",HttpStatus.ALREADY_REPORTED);
                    }
                    if (aadhaarNumber.length() == 12) {
                        user.setAadhaarNumber(aadhaarNumber);
                        user.setKycDone(true);
                        userService.saveUser(user);
                        return new ResponseEntity<>("KYC verification successful.",HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>("KYC verification failed.", HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
                }
        } catch (Exception e) {
            return new ResponseEntity<>("Exception occured while updating user. Reason >>>>>>>>" + e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
