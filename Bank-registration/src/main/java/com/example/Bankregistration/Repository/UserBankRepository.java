package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.UserBankProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserBankRepository extends JpaRepository<UserBankProperties,String> {

}

