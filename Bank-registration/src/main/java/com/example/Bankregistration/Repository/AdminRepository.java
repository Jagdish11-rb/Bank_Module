package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,String> {

    Optional<Admin> findById(String id);

    Optional<Admin> findByName(String name);

    @Query("select a.name from Admin a")
    public List<String> getAllAdmins();
}

