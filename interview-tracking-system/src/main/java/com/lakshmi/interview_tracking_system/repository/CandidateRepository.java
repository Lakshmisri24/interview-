package com.lakshmi.interview_tracking_system.repository;

import com.lakshmi.interview_tracking_system.dao.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate,Long> {
    Optional<Candidate> findByEmail(String email);
}
