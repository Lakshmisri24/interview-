package com.lakshmi.interview_tracking_system;

import com.lakshmi.interview_tracking_system.dao.Candidate;
import com.lakshmi.interview_tracking_system.dao.Interview;
import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.dto.Rating;
import com.lakshmi.interview_tracking_system.repository.CandidateRepository;
import com.lakshmi.interview_tracking_system.repository.InterviewRepository;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import com.lakshmi.interview_tracking_system.service.PanelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PanelServiceTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private InterviewRepository interviewRepo;
    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private PanelService panelService;

    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = () -> "user@example.com";
    }

    // ==== getInterviews ====
    @Test
    void getInterviews_returnsInterviews() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setRoleId(2L); // TECH panel

        Interview iv = new Interview();
        iv.setId(10L);

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(interviewRepo.findByAssignedUserIdAndStatusIn(eq(1L), any()))
                .thenReturn(List.of(iv));

        ResponseEntity<?> resp = panelService.getInterviews(principal);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat((List<?>) resp.getBody()).hasSize(1);
    }

    // ==== getCandidate ====
    @Test
    void getCandidate_returnsCandidate() {
        Candidate cand = new Candidate();
        cand.setId(5L);

        when(candidateRepository.findById(5L)).thenReturn(Optional.of(cand));

        ResponseEntity<?> resp = panelService.getCandidate(5L);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(((Optional<?>) resp.getBody()).get()).isEqualTo(cand);
    }

    // ==== submitRating ====
    @Test
    void submitRating_successForTech() {
        User user = new User();
        user.setId(1L);
        user.setRoleId(2L); // tech panel
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Interview iv = new Interview();
        iv.setId(100L);
        iv.setAssignedUserId(1L);
        iv.setStatus("TECH_SCHEDULED");

        when(interviewRepo.findById(100L)).thenReturn(Optional.of(iv));
        when(interviewRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Rating rating = new Rating();
        rating.setRating(4);

        ResponseEntity<?> resp = panelService.submitRating(rating, 100L, principal);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        Interview saved = (Interview) resp.getBody();
        assertThat(saved.getTechRating()).isEqualTo(4);
        assertThat(saved.getStatus()).isEqualTo("TECH_COMPLETED");
    }

    @Test
    void submitRating_invalidInterview_returnsBadRequest() {
        User user = new User();
        user.setId(1L);
        user.setRoleId(2L);
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        when(interviewRepo.findById(99L)).thenReturn(Optional.empty());

        Rating rating = new Rating();
        rating.setRating(3);

        ResponseEntity<?> resp = panelService.submitRating(rating, 99L, principal);

        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).isEqualTo("Invalid interview");
    }

    @Test
    void submitRating_invalidRating_returnsBadRequest() {
        User user = new User();
        user.setId(1L);
        user.setRoleId(2L);
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Interview iv = new Interview();
        iv.setId(100L);
        iv.setAssignedUserId(1L);
        iv.setStatus("TECH_SCHEDULED");

        when(interviewRepo.findById(100L)).thenReturn(Optional.of(iv));

        Rating rating = new Rating();
        rating.setRating(10); // invalid

        ResponseEntity<?> resp = panelService.submitRating(rating, 100L, principal);

        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).isEqualTo("Invalid rating");
    }
}

