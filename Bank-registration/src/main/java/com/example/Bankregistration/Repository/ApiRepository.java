package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.ApiPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiRepository extends JpaRepository<ApiPartner,String> {
    Optional<ApiPartner> findByUsername(String name);

    @Query("select a.username from ApiPartner a")
    List<String> findAllApiUsers();


    @Query(value = "select username from api_partner where admin_id = ? ",nativeQuery = true)
    List<String> findApiUsersOfSingleAdmin(@Param("admin_id") String id);
}

