package com.admin.repository;

import com.admin.model.entity.UserProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProductDetailsRepo extends JpaRepository<UserProductDetails, Integer> {

}
