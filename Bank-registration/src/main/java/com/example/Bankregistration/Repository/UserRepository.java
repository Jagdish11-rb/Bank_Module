package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.UserProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserProperties,String> {

    @Query("select u.user_name from UserProperties u")
    List<String> findAllOnboardedUsers();

    Optional<UserProperties> findByMobileNumber(String mobileNumber);

    UserProperties findByEmail(String email);
}

