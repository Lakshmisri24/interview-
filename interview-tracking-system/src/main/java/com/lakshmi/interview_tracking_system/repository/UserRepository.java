package com.lakshmi.interview_tracking_system.repository;

import com.lakshmi.interview_tracking_system.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
   Boolean existsByEmail(String email);
}
