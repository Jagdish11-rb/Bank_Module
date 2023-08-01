package com.example.Bankregistration.JWT;

import com.example.Bankregistration.Model.Request.AdminRequest;
import com.example.Bankregistration.Model.Request.UserLoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtGenerator implements JwtGeneratorInterface{

    @Value("${jwt.secret}")
    private String secret;

    @Value("${app.jwtToken.message}")
    private String message;

    @Value("${app.jwtScope.message}")
    private String scope;

    @Override
    public HashMap<String, String> generateToken(AdminRequest adminRequest) {

        HashMap<String,Object> map = new HashMap<>();
        map.put("id",adminRequest.getId());
        map.put("user_name",adminRequest.getUser_name());

        String jwtToken="";
        jwtToken= Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
        HashMap<String,String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token",jwtToken);
        jwtTokenGen.put("message",message);
        jwtTokenGen.put("scope",scope);

        return jwtTokenGen;

    }
    @Override
    public Map<String, String> generateToken(UserLoginRequest loginRequest) {
        String jwtToken = "";
        jwtToken = Jwts.builder()
                .setSubject(loginRequest.getPassword())
                .setId(loginRequest.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        Map<String, String> jwtTokenGen = new HashMap<>();
        jwtTokenGen.put("token", jwtToken);
        jwtTokenGen.put("message", message);
        jwtTokenGen.put("scope", scope);
        return jwtTokenGen;
    }

    public String getTokenFromAuthorization(HttpServletRequest httpServletRequest) {
        String auth = httpServletRequest.getHeader("Authorization");
        String token = auth.substring(7);
        return token;
    }

    public Claims getDataFromToken(String auth) {
        Claims claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(auth).getBody();;
        return claims;
    }

    public Boolean isTokenExpired(String token) {
        Claims claims = getDataFromToken(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // If the token is invalid or has expired, an exception will be thrown
            return false;
        }
    }
}
