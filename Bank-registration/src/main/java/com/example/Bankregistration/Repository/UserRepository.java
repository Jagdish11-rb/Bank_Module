package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.UserBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserBankDetails,String> {

    @Query("select u.user_name from UserBankDetails u")
    List<String> findAllOnboardedUsers();

    Optional<UserBankDetails> findByMobileNumber(String mobileNumber);
}

