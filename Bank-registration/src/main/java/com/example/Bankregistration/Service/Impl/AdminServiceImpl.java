package com.example.Bankregistration.Service.Impl;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Entity.UserProperties;
import com.example.Bankregistration.Exception.CustomException;
import com.example.Bankregistration.Exception.InvalidCredentialsException;
import com.example.Bankregistration.Exception.UserNotFoundException;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.LoginRequest;
import com.example.Bankregistration.RegUtils.Utils;
import com.example.Bankregistration.Repository.AdminRepository;
import com.example.Bankregistration.Service.AdminService;
import com.example.Bankregistration.Service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private ApiService apiService;

    @Override
    public void authorizeRequest(String secret_key) {
        if(secret_key==null || secret_key.isEmpty()) throw new CustomException("Please provide a secret_key");
        if(!(secret_key.matches(Utils.SECRET_CODE))) throw new CustomException("Please provide a valid secret_key");
    }

    @Override
    public HashMap<Boolean,String> onboardAdmin(AdminRequest adminRequest) {
        log.info("Request received to onboard : " + adminRequest);
        Admin admin = adminRepository.findById(adminRequest.getId()).orElse(null);
        HashMap<Boolean,String> map = new HashMap<>();
        if (!(admin==null)) {
            map.put(false,"Admin already present with this given id.");
            log.info("Admin already present with this given id.");
            return map;
        } else {
            map.clear();
            Admin admin2 = adminRepository.findByName(adminRequest.getUser_name()).orElse(null);
            if (admin2==null) {
                Admin newAdmin = new Admin();
                newAdmin.setUserId(adminRequest.getId());
                newAdmin.setName(adminRequest.getUser_name());
                newAdmin.setPassword(adminRequest.getPassword());

                adminRepository.save(newAdmin);
                map.put(true,"Admin onboarded successfully.");
                log.info("Admin onboarded successfully.");
                return map;
            } else {
                map.clear();
                log.info("Username already exists.");
                map.put(false,"Username already exists.");
                return map;
            }
        }
    }


    @Override
    public Optional<Admin> findAdminById(String id) {
        return adminRepository.findById(id);
    }

    @Override
    public List<String> getAllAdmins() {
        return adminRepository.getAllAdmins();
    }


    @Override
    public List<String> getAllApiUsersOfAnAdmin(String id) {
        List<String> apiList = apiService.getAllApiUsersOfAdmin(id);
        return apiList;
    }

    @Override
    public ApiPartner findApiUserByName(String name) {
        ApiPartner apiPartner = apiService.findApiUserByName(name);
        return apiPartner;
    }

    @Override
    public void removeAdmin(Admin admin) {
        adminRepository.delete(admin);
    }

    @Override
    public void changeDetailsOfApiUser(ApiPartner apiPartner) {
        apiService.changeDetailsForDeletedAdmin(apiPartner);
    }

    @Override
    public void authenticateAdmin(LoginRequest loginRequest) {
        Admin admin  = adminRepository.findById(loginRequest.getId()).orElse(null);
        if(admin==null || !(admin.getPassword().matches(loginRequest.getPassword())))
            throw new InvalidCredentialsException("Invalid credentials");
    }
}
