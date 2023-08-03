package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.ForgotPasswordOtpProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<ForgotPasswordOtpProperties,String> {

}

