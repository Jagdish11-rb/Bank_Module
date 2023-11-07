package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface AdminService{
    void authorizeRequest(String secret_key);

    HashMap<Boolean,String> onboardAdmin(AdminRequest adminRequest);

    Optional<Admin> findAdminById(String id);

    List<String> getAllAdmins();

    List<String> getAllApiUsersOfAnAdmin(String id);

    ApiPartner findApiUserByName(String s);

    void removeAdmin(Admin admin);

    void changeDetailsOfApiUser(ApiPartner apiPartner);

    void authenticateAdmin(LoginRequest loginRequest);
}
