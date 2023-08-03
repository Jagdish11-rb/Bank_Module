package com.example.Bankregistration.Service.Impl;


import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.JWT.JwtGenerator;
import com.example.Bankregistration.Model.Request.ApiRequest;
import com.example.Bankregistration.Repository.ApiRepository;
import com.example.Bankregistration.Service.AdminService;
import com.example.Bankregistration.Service.ApiService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ApiServiceImpl implements ApiService {


    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private JwtGenerator jwtGenerator;


    @Override
    public HashMap<Integer,String> onboardApiPartner(ApiRequest apiRequest, Claims tokenData) {
        log.info("Request received to onboard : " + apiRequest);
        ApiPartner apiPartner = apiRepository.findById(apiRequest.getApi_user_id()).orElse(null);
        HashMap<Integer,String> map = new HashMap();
        if (!(apiPartner==null)) {
            log.info("ApiPartner already present with this given id.");
            map.put(1,"ApiPartner already present with this given id.");
            return map;
        } else {
            map.clear();
            ApiPartner apiPartner2 = apiRepository.findByUsername(apiRequest.getApi_user_name()).orElse(null);
            if (apiPartner2==null) {
                ApiPartner newApiPartner = new ApiPartner();

                newApiPartner.setId(apiRequest.getApi_user_id());
                newApiPartner.setUsername(apiRequest.getApi_user_name());
                newApiPartner.setAdminName(tokenData.get("user_name", String.class));
                newApiPartner.setAdminId(tokenData.get("id", String.class));
                newApiPartner.setActive(true);

                apiRepository.save(newApiPartner);
                log.info("ApiPartner onboarded successfully.");
                map.put(0,"ApiPartner onboarded successfully.");
                return map;
            } else {
                map.clear();
                log.info("Username already exists.");
                map.put(-1,"Username already exists.");
                return map;
            }
        }
    }

    @Override
    public List<String> retreiveApiPartners() {
        return apiRepository.findAllApiUsers();
    }

    @Override
    public Optional<ApiPartner> findApiuserById(String apiUserId) {
        return apiRepository.findById(apiUserId);
    }

    @Override
    public String deleteApiUser(String id) {
        apiRepository.deleteById(id);
        return "Api user removed successfully.";
    }

    @Override
    public ApiPartner findApiUserByName(String apiUserName) {
        return apiRepository.findByUsername(apiUserName).orElse(null);
    }

    @Override
    public void saveApiUser(ApiPartner apiPartner) {
        apiRepository.save(apiPartner);
    }

    @Override
    public List<String> getAllApiUsersOfAdmin(String id) {
        List<String> apiList = apiRepository.findApiUsersOfSingleAdmin(id);
        return apiList;
    }

    @Override
    public void changeDetailsForDeletedAdmin(ApiPartner apiPartner) {
        apiPartner.setAdminId(null);
        apiPartner.setAdminName(null);
        apiPartner.setActive(false);
        apiRepository.save(apiPartner);
    }


}
