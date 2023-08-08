package com.example.Bankregistration.Controller;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import com.example.Bankregistration.Model.Response.AdminResponse;
import com.example.Bankregistration.Service.AdminService;
import com.example.Bankregistration.Service.ApiService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private ApiService apiService;



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

    @PostMapping("admin/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            Admin admin = adminService.authenticateAdmin(loginRequest);
            if(admin!=null){
                String token = jwtGenerator.generateToken(loginRequest);
                log.info(token);
                Cookie cookie = new Cookie("my_admin_cookie",token);
                cookie.setHttpOnly(true);
                cookie.setMaxAge((int) (jwtGenerator.getExpiration()/100));
                response.addCookie(cookie);
                return new ResponseEntity<>("Admin logged in successfully.",HttpStatus.ACCEPTED);
            }else{
                return new ResponseEntity<>("Invalid credentials",HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while logging in admin.  Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("admin/get-logged-in-admin-details")
    public ResponseEntity<?> getAdminDetails(@CookieValue(value="my_admin_cookie",required = false) String cookie){
        try{
            log.info(cookie);
            boolean isValid = jwtGenerator.validateToken(cookie);
            if(isValid==true){
                String adminId = jwtGenerator.getDataFromToken(cookie).getId();
                Admin admin = adminService.findAdminById(adminId).orElse(null);
                if(admin!=null){
                    return new ResponseEntity<>(admin,HttpStatus.ACCEPTED);
                }else{
                    return new ResponseEntity<>("Admin not found.",HttpStatus.NOT_ACCEPTABLE);
                }
            }else{
                return new ResponseEntity<>("Token expired.",HttpStatus.GONE);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while fetching data. Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/admin/remove-onboarded-admin")
    public ResponseEntity<?> removeAdmin(@CookieValue(value="my_admin_cookie",required = false) String cookie){
        try{
            String adminId = jwtGenerator.getDataFromToken(cookie).getId();
            Admin admin = adminService.findAdminById(adminId).orElse(null);
            if(admin==null){
                return new ResponseEntity<>("No admin onboarded with this admin id.",HttpStatus.NOT_FOUND);
            }else{
                List<String> apiUsers = adminService.getAllApiUsersOfAnAdmin(adminId);
                log.info(apiUsers.toString());
                for(int i=0;i<apiUsers.size();i++){
                    ApiPartner apiPartner = adminService.findApiUserByName(apiUsers.get(i));
                    adminService.changeDetailsOfApiUser(apiPartner);
                }
                adminService.removeAdmin(admin);
                return new ResponseEntity<>("Admin removed successfully.",HttpStatus.ACCEPTED);
            }
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while deleting admin. Reason >>>>"+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("admin/admin-logout")
    public ResponseEntity<?> adminLogout(@CookieValue(value="my_admin_cookie",required = false) String cookie,HttpServletResponse httpServletResponse){
        try{
            log.info(cookie);
            if(cookie!=null){
                Cookie newCookie = new Cookie("my_admin_cookie",null);
                newCookie.setMaxAge(0);
                httpServletResponse.addCookie(newCookie);
                httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                httpServletResponse.setHeader("Pragma", "no-cache");
                httpServletResponse.setHeader("Expires", "0");
            }
            return new ResponseEntity<>("Logged out successfully.",HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>("Exception occured while logging out. Reason : "+e.getMessage(),HttpStatus.CONFLICT);
        }
    }

    @PostMapping("admin/validate-token")
    public boolean validateToken(@RequestBody String token){
        return jwtGenerator.validateToken(token);
    }
}
