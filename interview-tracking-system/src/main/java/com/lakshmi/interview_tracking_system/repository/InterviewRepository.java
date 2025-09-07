package com.lakshmi.interview_tracking_system.repository;

import com.lakshmi.interview_tracking_system.dao.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview,Long> {
    List<Interview> findByCandidateId(Long candidateId);
    Optional<Interview> findTopByCandidateIdOrderByScheduledTimeDesc(Long candidateId);
    List<Interview> findByAssignedUserIdAndStatusIn(Long userId, Collection<String> statuses);
}
