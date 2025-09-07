package com.lakshmi.interview_tracking_system.repository;

import com.lakshmi.interview_tracking_system.dao.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {


    Optional<Role> findByName(String aLong);
}
