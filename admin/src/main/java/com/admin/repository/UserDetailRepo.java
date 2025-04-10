package com.admin.repository;

import com.admin.model.entity.CustomerCredentialManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepo extends JpaRepository<CustomerCredentialManager,Integer> {

    @Query(value = "select id from customer_credential_manager ccm where mobile_number =:mobile_number ;", nativeQuery = true)
    Integer findByMobileNumber(String mobile_number);

}
