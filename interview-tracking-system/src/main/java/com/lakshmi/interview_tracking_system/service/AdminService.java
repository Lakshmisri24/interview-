package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.Candidate;
import com.lakshmi.interview_tracking_system.dao.Interview;
import com.lakshmi.interview_tracking_system.dao.Role;
import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.dto.*;
import com.lakshmi.interview_tracking_system.repository.CandidateRepository;
import com.lakshmi.interview_tracking_system.repository.InterviewRepository;
import com.lakshmi.interview_tracking_system.repository.RoleRepository;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service

public class AdminService {
    private static final Set<String> ADMIN_SETTABLE_STATUSES = Set.of("HR_SCHEDULED", "SELECTED", "CANCELLED", "REJECTED");
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CandidateRepository candidateRepo;
    @Autowired
    private InterviewRepository interviewRepo;

    public ResponseEntity<?> createAdminTech(CreateAdminTechRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("duplicate email"); // controller will handle as bad request
        }
        Role role = roleRepo.findByName(request.getRoleName()).orElse(null);
        if (role == null) {
            return ResponseEntity.badRequest().body("Invalid ROle"); // controller will handle
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleId(role.getId());
        user.setActive(request.getIsActive());
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepo.findAll();
        List<CreateAdminTechResponse> result = new ArrayList<>();
        for (User u : users) {
            Optional<Role> roleOpt = roleRepo.findById(u.getRoleId());
            String roleName = roleOpt.map(Role::getName).orElse(null);

            result.add(CreateAdminTechResponse.builder().id(u.getId()).fullName(u.getFullName()).email(u.getEmail()).roleName(roleName).isActive(u.isActive()).build());
        }
        return ResponseEntity.ok(result);
    }


    public ResponseEntity<?> deleteUser(Long id) {

        if (!userRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");

    }

    public ResponseEntity<?> createCandidate(CandidateRequest req) {
        if (req.getEmail() != null && candidateRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("duplicate email"); // duplicate email

        }
        Candidate saved = candidateRepo.save(Candidate.builder().fullName(req.getFullName()).email(req.getEmail()).phone(req.getPhone()).primarySkills(req.getPrimarySkills()).secondarySkills(req.getSecondarySkills()).experience(req.getExperience()).qualification(req.getQualification()).designation(req.getDesignation()).noticePeriod(req.getNoticePeriod()).location(req.getLocation()).build());
        return ResponseEntity.ok("Candidate added successfully");
    }


    public ResponseEntity<?> getCandidate() {
        return ResponseEntity.ok(candidateRepo.findAll());
    }

    public ResponseEntity<?> getCandidates(Long id) {

        if (!candidateRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("candidate not found");
        }
        Optional<Candidate> candidate = candidateRepo.findById(id);
        return ResponseEntity.ok(candidate.get());

    }

    public InterviewResponse scheduleInterview(ScheduleInterviewRequest req) {
        if (req.getCandidateId() == null || req.getScheduledTime() == null) return null;
        if (candidateRepo.findById(req.getCandidateId()).isEmpty()) return null;

        // active = not final
        java.util.Set<String> ACTIVE = java.util.Set.of("TECH_SCHEDULED", "TECH_COMPLETED", "HR_SCHEDULED", "HR_COMPLETED");

        java.util.List<Interview> existing = interviewRepo.findByCandidateId(req.getCandidateId());
        for (Interview iv : existing) {
            String st = iv.getStatus();
            if (st != null && ACTIVE.contains(st)) return null; // can’t schedule while active exists
        }

        Interview saved = interviewRepo.save(Interview.builder().candidateId(req.getCandidateId()).scheduledTime(req.getScheduledTime()).status("TECH_SCHEDULED")   // always start with TECH
                .assignedUserId(null).build());

        return InterviewResponse.builder().id(saved.getId()).candidateId(saved.getCandidateId()).assignedUserId(saved.getAssignedUserId()).scheduledTime(saved.getScheduledTime()).status(saved.getStatus()).build();

    }

    public InterviewResponse updateInterview(Long id, UpdateInterviewRequest req) {
        Optional<Interview> opt = interviewRepo.findById(id);
        if (opt.isEmpty()) return null;

        Interview interview = opt.get();

        // 2) Status (admin can set only HR_SCHEDULED, SELECTED, CANCELLED, REJECTED)
        if (req.getStatus() != null) {
            String newStatus = req.getStatus();
            if (!ADMIN_SETTABLE_STATUSES.contains(newStatus)) {
                return null;
            }

            if ("HR_SCHEDULED".equals(newStatus) && !isTechCompleted(interview)) {
                return null;
            }

            // SELECTED only if HR is completed
            if ("SELECTED".equals(newStatus) && !isHrCompleted(interview)) {
                return null;
            }

            if ("HR_SCHEDULED".equals(newStatus) && isHrCompleted(interview)) {
                return null; // can't re-schedule HR after HR completed
            }
            interview.setStatus(newStatus);
        }

        // 3) (Re)assign panel user — must match the (possibly updated) status
        if (req.getAssignedUserId() != null) {
            Optional<User> panelOpt = userRepo.findById(req.getAssignedUserId());
            if (panelOpt.isEmpty()) return null;

            User panelUser = panelOpt.get();

            Optional<Role> roleOpt = roleRepo.findById(panelUser.getRoleId());
            if (roleOpt.isEmpty()) return null;

            Role role = roleOpt.get();
            String roleName = role.getName(); // TECH_PANEL / HR_PANEL / ADMIN

            if (!panelUser.isActive()) {
                return null;
            }

            if (!"TECH_PANEL".equals(roleName) && !"HR_PANEL".equals(roleName)) {
                return null; // only panel users can be assigned
            }
            String statusForCheck = interview.getStatus(); // after any status update above
            if (!canAssignForStatus(statusForCheck, roleName)) {
                return null; // TECH only for TECH_SCHEDULED, HR only for HR_SCHEDULED
            }
            interview.setAssignedUserId(panelUser.getId());
        }

        Interview saved = interviewRepo.save(interview);

        return InterviewResponse.builder().id(saved.getId()).candidateId(saved.getCandidateId()).assignedUserId(saved.getAssignedUserId()).scheduledTime(saved.getScheduledTime()).status(saved.getStatus()).hrRating(saved.getHrRating()).techRating(saved.getTechRating()).build();

    }

    private boolean isTechCompleted(Interview interview) {
        return interview.getTechRating() != null;
    }

    private boolean isHrCompleted(Interview interview) {
        return interview.getHrRating() != null;
    }

    private boolean canAssignForStatus(String status, String panelRoleName) {
        if ("TECH_SCHEDULED".equals(status)) {
            return "TECH_PANEL".equals(panelRoleName);
        }
        if ("HR_SCHEDULED".equals(status)) {
            return "HR_PANEL".equals(panelRoleName);
        }
        return false; // no assigning in other states
    }

    public CandidateDetailResponse getCandidateWithInterview(Long candidateId) {
        Optional<Candidate> optionalCandidate = candidateRepo.findById(candidateId);
        if (optionalCandidate.isEmpty()) {
            return null;
        }

        Candidate c = optionalCandidate.get();

        Optional<Interview> ivOpt = interviewRepo.findTopByCandidateIdOrderByScheduledTimeDesc(c.getId());


        return CandidateDetailResponse.builder().candidate(c).interview(ivOpt.orElse(null)) // null if no interview exists at all
                .build();
    }
}

