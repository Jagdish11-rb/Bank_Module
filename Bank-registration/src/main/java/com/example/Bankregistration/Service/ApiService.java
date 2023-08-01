package com.example.Bankregistration.Service;

import com.example.Bankregistration.Entity.Admin;
import com.example.Bankregistration.Entity.ApiPartner;
import com.example.Bankregistration.Model.Request.ApiRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ApiService {

    HashMap<Integer,String> onboardApiPartner(ApiRequest apiRequest, Claims tokenData);

    List<String> retreiveApiPartners();

    Optional<ApiPartner> findApiuserById(String apiUserId);

    String deleteApiUser(String id);

    ApiPartner findApiUserByName(String apiUserName);

    void saveApiUser(ApiPartner apiPartner);

    String getTokenFromAuthorization(HttpServletRequest httpServletRequest);

    boolean validateToken(String token);

    Claims getDataFromToken(String token);

    List<String> getAllApiUsersOfAdmin(String id);

    void changeDetailsForDeletedAdmin(ApiPartner apiPartner);

}
