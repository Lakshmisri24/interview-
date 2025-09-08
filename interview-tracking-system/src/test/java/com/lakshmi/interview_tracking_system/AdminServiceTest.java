package com.lakshmi.interview_tracking_system;


import com.lakshmi.interview_tracking_system.dao.*;
import com.lakshmi.interview_tracking_system.dto.*;
import com.lakshmi.interview_tracking_system.repository.*;
import com.lakshmi.interview_tracking_system.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdminServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private RoleRepository roleRepo;
    @Mock private CandidateRepository candidateRepo;
    @Mock private InterviewRepository interviewRepo;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===== Users =====

    @Test
    void createAdminTech_duplicateEmail_returnsBadRequest() {
        CreateAdminTechRequest req = new CreateAdminTechRequest();
        req.setEmail("dup@example.com");

        when(userRepo.existsByEmail("dup@example.com")).thenReturn(true);

        ResponseEntity<?> resp = adminService.createAdminTech(req);

        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isEqualTo("duplicate email");
    }

    @Test
    void createAdminTech_valid_returnsCreatedUser() {
        CreateAdminTechRequest req = new CreateAdminTechRequest();
        req.setFullName("Lakshmi");
        req.setEmail("lakshmi@example.com");
        req.setPassword("secret");
        req.setRoleName("ADMIN");
        req.setIsActive(true);

        Role role = Role.builder().id(1L).name("ADMIN").build();
        when(userRepo.existsByEmail("lakshmi@example.com")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        User saved = User.builder()
                .id(99L).fullName("Lakshmi").email("lakshmi@example.com")
                .password("hashed").roleId(1L).isActive(true).build();

        when(userRepo.save(any(User.class))).thenReturn(saved);

        ResponseEntity<?> resp = adminService.createAdminTech(req);

        assertThat(resp.getStatusCodeValue()).isEqualTo(201);
        User body = (User) resp.getBody();
        assertThat(body.getFullName()).isEqualTo("Lakshmi");
        assertThat(body.getPassword()).isEqualTo("hashed");
    }

    // ===== Candidates =====

    @Test
    void createCandidate_duplicateEmail_returnsBadRequest() {
        CandidateRequest req = CandidateRequest.builder()
                .fullName("John")
                .email("john@example.com")
                .build();

        when(candidateRepo.findByEmail("john@example.com"))
                .thenReturn(Optional.of(new Candidate()));

        ResponseEntity<?> resp = adminService.createCandidate(req);

        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        assertThat(resp.getBody()).isEqualTo("duplicate email");
    }

    @Test
    void createCandidate_valid_returnsOk() {
        CandidateRequest req = CandidateRequest.builder()
                .fullName("John")
                .email("john@example.com")
                .location("Bangalore")
                .build();

        when(candidateRepo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(candidateRepo.save(any(Candidate.class)))
                .thenReturn(Candidate.builder().id(1L).fullName("John").build());

        ResponseEntity<?> resp = adminService.createCandidate(req);

        assertThat(resp.getStatusCodeValue()).isEqualTo(200);
        assertThat(resp.getBody()).isEqualTo("Candidate added successfully");
    }

    // ===== Interviews =====

    @Test
    void scheduleInterview_candidateNotFound_returnsNull() {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        req.setCandidateId(1L);
        req.setScheduledTime(OffsetDateTime.now());

        when(candidateRepo.findById(1L)).thenReturn(Optional.empty());

        InterviewResponse resp = adminService.scheduleInterview(req);

        assertThat(resp).isNull();
    }

    @Test
    void scheduleInterview_valid_returnsInterviewResponse() {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        req.setCandidateId(1L);
        req.setScheduledTime(OffsetDateTime.now());

        when(candidateRepo.findById(1L)).thenReturn(Optional.of(new Candidate()));
        when(interviewRepo.findByCandidateId(1L)).thenReturn(Collections.emptyList());

        Interview saved = Interview.builder()
                .id(10L).candidateId(1L)
                .status("TECH_SCHEDULED")
                .scheduledTime(req.getScheduledTime())
                .build();

        when(interviewRepo.save(any(Interview.class))).thenReturn(saved);

        InterviewResponse resp = adminService.scheduleInterview(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo("TECH_SCHEDULED");
        assertThat(resp.getCandidateId()).isEqualTo(1L);
    }

    @Test
    void updateInterview_invalidStatus_returnsNull() {
        UpdateInterviewRequest req = new UpdateInterviewRequest();
        req.setStatus("INVALID");

        Interview interview = Interview.builder().id(1L).status("TECH_SCHEDULED").build();
        when(interviewRepo.findById(1L)).thenReturn(Optional.of(interview));

        InterviewResponse resp = adminService.updateInterview(1L, req);

        assertThat(resp).isNull();
    }
}
