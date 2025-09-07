package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.Candidate;
import com.lakshmi.interview_tracking_system.dao.Interview;
import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.dto.Rating;
import com.lakshmi.interview_tracking_system.repository.CandidateRepository;
import com.lakshmi.interview_tracking_system.repository.InterviewRepository;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class PanelService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private InterviewRepository interviewRepo;
    @Autowired
    public CandidateRepository candidateRepository;

    public ResponseEntity<?> getInterviews(Principal principal) {
      String email=  principal.getName();
        Optional<User> byEmail = userRepo.findByEmail(email);
        List<String> status = byEmail.get().getRoleId() == 3 ? List.of("HR_SCHEDULED") :
                List.of("TECH_SCHEDULED");
        List<Interview> byAssignedUserIdAndStatusIn = interviewRepo.findByAssignedUserIdAndStatusIn(byEmail.get().getId(), status);
        return ResponseEntity.ok(byAssignedUserIdAndStatusIn);
    }

    public ResponseEntity<?> getCandidate(Long id) {
        Optional<Candidate> byId = candidateRepository.findById(id);
        return ResponseEntity.ok(byId);
    }

    public ResponseEntity<?> submitRating(Rating req, Long interviewId, Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        Long userId = user.getId();
        boolean isTech = user.getRoleId() == 2;

        Optional<Interview> opt = interviewRepo.findById(interviewId);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("Invalid interview");

        Interview iv = opt.get();

        // must be assigned to this HR user
        if (iv.getAssignedUserId() == null || !iv.getAssignedUserId().equals(userId)) {
            return ResponseEntity.badRequest().body("not assigned");
        }

        // must be in HR_SCHEDULED
        if (!"HR_SCHEDULED".equals(iv.getStatus()) && !"TECH_SCHEDULED".equals(iv.getStatus())) {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        // rating must be 1..5
        if (req.getRating() < 1 || req.getRating() > 5) {
            return ResponseEntity.badRequest().body("Invalid rating");
        }

        if (isTech) {
            iv.setTechRating(req.getRating());
            iv.setStatus("TECH_COMPLETED");
        } else {
            iv.setHrRating(req.getRating());
            iv.setStatus("HR_COMPLETED");
        }
        Interview interview = interviewRepo.save(iv);
        return ResponseEntity.ok(interview);
    }
}

