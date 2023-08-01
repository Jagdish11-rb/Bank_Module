package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Model.Request.AdminRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface AdminService{
    boolean authorizeRequest(String secret_key);

    HashMap<Integer,String> onboardAdmin(AdminRequest adminRequest);

    boolean validateRequest(AdminRequest adminRequest);

    Optional<Admin> findAdminById(String id);

    List<String> getAllAdmins();

    Object generateToken(AdminRequest adminRequest);

    String getTokenFromAuthorization(HttpServletRequest httpServletRequest);

    boolean isTokenExpired(String auth);

    Claims getDataFromToken(String auth);

    boolean validateToken(String token);

    List<String> getAllApiUsersOfAnAdmin(String id);

    ApiPartner findApiUserByName(String s);
    

    void removeAdmin(Admin admin);

    void changeDetailsOfApiUser(ApiPartner apiPartner);
}
