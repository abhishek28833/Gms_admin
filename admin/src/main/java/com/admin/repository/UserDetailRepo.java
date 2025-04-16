package com.admin.repository;

import com.admin.model.entity.CustomerDetailManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailRepo extends JpaRepository<CustomerDetailManager,String> {

    @Query(value = "select * from customer_detail_manager cdm where username=:username ;", nativeQuery = true)
    CustomerDetailManager getUserData(String username);

    @Query(value = "select code from master_gms_roles mgr where id =:id ;", nativeQuery = true)
    String getRoleById(Integer id);

}
