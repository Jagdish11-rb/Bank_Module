package com.example.Bankregistration.Controller;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.ApiRequest;
import com.example.Bankregistration.Model.Response.ApiResponse;
import com.example.Bankregistration.Service.AdminService;
import com.example.Bankregistration.Service.ApiService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@Slf4j
public class ApiController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtGenerator jwtGenerator;


    @PostMapping("/admin/onboard-api-user")
    public ResponseEntity<?> onboardApiUser(@RequestBody ApiRequest apiRequest,@CookieValue(value="my_admin_cookie",required = false) String token){
        ApiResponse apiResponse = new ApiResponse();
        try{
            log.info(token);
            if(jwtGenerator.validateToken(token)){
                Claims tokenData = jwtGenerator.getDataFromToken(token);
                Optional<Admin> admin = adminService.findAdminById(tokenData.getId());
                if(!admin.isEmpty()){
                    HashMap<Integer,String> map = apiService.onboardApiPartner(apiRequest,tokenData);
                    if(map.containsKey(0)){
                        apiResponse.setApi_user_id(apiRequest.getApi_user_id());
                        apiResponse.setApi_user_name(apiRequest.getApi_user_name());
                        apiResponse.setMessage("ApiPartner onboarded successfully.");
                        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
                    }else if(map.containsKey(1)){
                        apiResponse.setApi_user_id(apiRequest.getApi_user_id());
                        apiResponse.setApi_user_name(apiRequest.getApi_user_name());
                        apiResponse.setMessage("ApiPartner already present with this id.");
                        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_ACCEPTABLE);
                    }else{
                        apiResponse.setApi_user_id(apiRequest.getApi_user_id());
                        apiResponse.setApi_user_name(apiRequest.getApi_user_name());
                        apiResponse.setMessage("Api username already exists.");
                        return new ResponseEntity<>(apiResponse,HttpStatus.NOT_ACCEPTABLE);
                    }
                }else{
                    apiResponse.setApi_user_id(apiRequest.getApi_user_id());
                    apiResponse.setApi_user_name(apiRequest.getApi_user_name());
                    apiResponse.setMessage("Admin details not found.");
                    return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
                }
            }else{
                apiResponse.setApi_user_id(apiRequest.getApi_user_id());
                apiResponse.setApi_user_name(apiRequest.getApi_user_name());
                apiResponse.setMessage("Invalid token or Token expired.");
                return new ResponseEntity<>(apiResponse,HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            apiResponse.setApi_user_id(apiRequest.getApi_user_id());
            apiResponse.setApi_user_name(apiRequest.getApi_user_name());
            apiResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(apiResponse,HttpStatus.CONFLICT);
        }
    }

    @GetMapping("api/all-api-users")
    public ResponseEntity<?> getAllApiUsers(){
        try{
            List<String> list = apiService.retreiveApiPartners();
            return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching data. Reason >>>>>>>>>>>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("admin/remove-api-user/{id}")
    public ResponseEntity<?> removeApiUser(@PathVariable String id,@CookieValue(value="my_admin_cookie",required = false) String token){
        try{
            log.info(token);
            boolean isValid = jwtGenerator.validateToken(token);
            if(isValid==true){
                Optional<ApiPartner> apiPartner = apiService.findApiuserById(id);
                if(!apiPartner.isEmpty()){
                    Claims tokenData = jwtGenerator.getDataFromToken(token);
                    if(!(tokenData.getId().matches(apiPartner.get().getAdminId()) )
                            || !(tokenData.getSubject().matches(apiPartner.get().getAdminName()))){
                        return new ResponseEntity<>("Invalid token data.",HttpStatus.NOT_ACCEPTABLE);
                    }else{
                        String response = apiService.deleteApiUser(id);
                        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
                    }
                }else{
                    return new ResponseEntity<>("No api user onboarded with this id.",HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>("Invalid token.",HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured. Reason >>>>>>>> "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/api/activate-api_user")
    public ResponseEntity<?> activatedApiUser(@RequestHeader("api_user_name") String api_user_name){
        try{
            ApiPartner apiPartner = apiService.findApiUserByName(api_user_name);
            if(apiPartner!=null){
                if(!(apiPartner.getAdminId()==null)){
                    apiPartner.setActive(true);
                    apiService.saveApiUser(apiPartner);
                    return new ResponseEntity<>("Api user activation successful.",HttpStatus.ACCEPTED);
                }else{
                    return new ResponseEntity<>("Api user can't activated.  Reason : Admin not found.",HttpStatus.NOT_FOUND);
                }
            }else{
                return new ResponseEntity<>("Api user not found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while activating api user.  Reason  >>>>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }

    }


}
