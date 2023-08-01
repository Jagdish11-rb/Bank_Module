package com.example.Bankregistration.Controller;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Response.AdminResponse;
import com.example.Bankregistration.Pojo.CustomClaims;
import com.example.Bankregistration.Service.AdminService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;


    @GetMapping("/welcome")
    public String message(){
        return "Welcome admin";
    }

    @PostMapping("/admin/onboard-admin")
    public ResponseEntity<?> onboardAdmin(@Valid @RequestBody AdminRequest adminRequest, @RequestHeader("secret_key") String secret_key){

        AdminResponse adminResponse = new AdminResponse();

        try{
            boolean isValid = adminService.authorizeRequest(secret_key);
            if(isValid==true){
               HashMap<Integer,String> map =  adminService.onboardAdmin(adminRequest);
                if(map.containsKey(0)){
                    adminResponse.setAdmin_id(adminRequest.getId());
                    adminResponse.setAdmin_user_name(adminRequest.getUser_name());
                    adminResponse.setMessage("Admin onboarded successfully.");
                    return new ResponseEntity<>(adminResponse, HttpStatus.CREATED);
                }else if(map.containsKey(1)){
                    adminResponse.setAdmin_id(adminRequest.getId());
                    adminResponse.setAdmin_user_name(adminRequest.getUser_name());
                    adminResponse.setMessage("Admin already present with this id.");
                    return new ResponseEntity<>(adminResponse,HttpStatus.NOT_ACCEPTABLE);
                }else{
                    adminResponse.setAdmin_id(adminRequest.getId());
                    adminResponse.setAdmin_user_name(adminRequest.getUser_name());
                    adminResponse.setMessage("Username already exists.");
                    return new ResponseEntity<>(adminResponse,HttpStatus.NOT_ACCEPTABLE);
                }
            }else{
                adminResponse.setAdmin_id(adminRequest.getId());
                adminResponse.setAdmin_user_name(adminRequest.getUser_name());
                adminResponse.setMessage("Unauthorized request.");
                return new ResponseEntity<>(adminResponse,HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }
        }catch(Exception e){
            adminResponse.setAdmin_id(adminRequest.getId());
            adminResponse.setAdmin_user_name(adminRequest.getUser_name());
            adminResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(adminResponse,HttpStatus.CONFLICT);
        }

    }

    @GetMapping("/admin/get-all-admins")
    public List<String> getAdmins(@RequestHeader("secret_key") String secret_key){

        List<String> list = new ArrayList<>();
        try{
            boolean isValid = adminService.authorizeRequest(secret_key);
            if(isValid==true){
                list=adminService.getAllAdmins();
                log.info(list.toString());
            }
        }catch(Exception e){
                log.info("Exception occured while fetching database.");
        }
        return list;
    }

    @PostMapping("/admin/generate-admin-token")
    public ResponseEntity<?> generateAdminToken(@Valid @RequestBody AdminRequest adminRequest, @RequestHeader("secret_key") String secret_key){
        try{
            boolean isValid = adminService.authorizeRequest(secret_key);
            if(isValid==true){
                boolean res = adminService.validateRequest(adminRequest);
                if(res!=true){
                    return new ResponseEntity<>("Invalid credentials",HttpStatus.NOT_ACCEPTABLE);
                }else{
                    log.info("Admin request : "+adminRequest);
                    return new ResponseEntity<>(adminService.generateToken(adminRequest),HttpStatus.ACCEPTED);
                }
            }else{
                return new ResponseEntity<>("Unauthorized request.",HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }
        }catch(Exception e){
                log.info("Exception occured while generating token.");
                return new ResponseEntity<>("Exception occured while generating token.",HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/admin/get-token-data")
    public ResponseEntity<?> getTokenData(HttpServletRequest httpServletRequest){
        CustomClaims claims = new CustomClaims();
        try{
            String token = adminService.getTokenFromAuthorization(httpServletRequest);
            boolean isExpired = adminService.isTokenExpired(token);
            if(isExpired!=true){
                Claims data = adminService.getDataFromToken(token);
                claims.setUserId(data.get("id", String.class));
                claims.setName(data.get("user_name", String.class));
                return new ResponseEntity<>(claims,HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("Token expired",HttpStatus.ACCEPTED);
            }
        }catch(Exception e){
                return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/admin/validateToken")
    public boolean validateToken(@RequestBody String token){
       return adminService.validateToken(token);
    }

    @PostMapping("/admin/remove-onboarded-user")
    public ResponseEntity<?> removeAdmin(@RequestBody AdminRequest adminRequest,@RequestHeader("secret_key") String secret_key){
        try{
            boolean isValid = adminService.authorizeRequest(secret_key);
            if(isValid==true){
                Admin admin = adminService.findAdminById(adminRequest.getId()).orElse(null);
                if(admin==null){
                    return new ResponseEntity<>("No admin onboarded with this admin id.",HttpStatus.NOT_FOUND);
                }else{
                    List<String> apiUsers = adminService.getAllApiUsersOfAnAdmin(adminRequest.getId());
                    log.info(apiUsers.toString());
                    for(int i=0;i<apiUsers.size();i++){
                        ApiPartner apiPartner = adminService.findApiUserByName(apiUsers.get(i));
                        adminService.changeDetailsOfApiUser(apiPartner);
                    }
                    adminService.removeAdmin(admin);
                    return new ResponseEntity<>("Admin removed successfully.",HttpStatus.ACCEPTED);
                }
            }else{
                return new ResponseEntity<>("Unauthorized request.",HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while deleting admin. Reason >>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }
}
