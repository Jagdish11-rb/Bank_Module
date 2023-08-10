package com.example.Bankregistration.Repository;

import com.example.Bankregistration.Entity.UserBankProperties;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBankRepository extends JpaRepository<UserBankProperties,String> {



//    @Query(value = "delete from UserBankProperties u where u.user_id = ?",nativeQuery = true)
//    void deleteAllByUserId(@Param("user_id")String userId);

    @Transactional
    void deleteAllByUserId(String user_id);

    UserBankProperties findByBankId(String id);

    @Transactional
    void deleteByBankId(String bankId);

    UserBankProperties findByAccountNumber(String accountNumber);
}

