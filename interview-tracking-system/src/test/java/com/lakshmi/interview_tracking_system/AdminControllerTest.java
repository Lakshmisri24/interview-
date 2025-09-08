package com.lakshmi.interview_tracking_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakshmi.interview_tracking_system.controller.AdminController;
import com.lakshmi.interview_tracking_system.dao.Candidate;
import com.lakshmi.interview_tracking_system.dto.CandidateRequest;
import com.lakshmi.interview_tracking_system.dto.CreateAdminTechRequest;
import com.lakshmi.interview_tracking_system.dto.CreateAdminTechResponse;
import com.lakshmi.interview_tracking_system.dto.InterviewResponse;
import com.lakshmi.interview_tracking_system.dto.ScheduleInterviewRequest;
import com.lakshmi.interview_tracking_system.dto.UpdateInterviewRequest;
import com.lakshmi.interview_tracking_system.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== Users ==========
    @Test
    void createUser_success() throws Exception {
        CreateAdminTechRequest req = new CreateAdminTechRequest();
        req.setFullName("lakshmi");
        req.setEmail("lakshmi@example.com");
        req.setPassword("pass");
        req.setRoleName("TECH_PANEL");
        req.setIsActive(true);

        CreateAdminTechResponse resp = CreateAdminTechResponse.builder()
                .id(1L)
                .fullName("lakshmi")
                .email("lakshmi@example.com")
                .roleName("TECH_PANEL")
                .isActive(true)
                .build();

        ResponseEntity<?> mockResponse = new ResponseEntity<>(resp, HttpStatus.CREATED);

        Mockito.doReturn(mockResponse).when(adminService).createAdminTech(any(CreateAdminTechRequest.class));


        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("lakshmi"))
                .andExpect(jsonPath("$.roleName").value("TECH_PANEL"));
    }

    @Test
    void getAllUsers_success() throws Exception {
        ResponseEntity<List<CreateAdminTechResponse>> responseEntity = ResponseEntity.ok(
                List.of(
                        CreateAdminTechResponse.builder()
                                .id(1L).fullName("lakshmi").email("lakshmi@example.com")
                                .roleName("TECH_PANEL").isActive(true).build()
                )
        );

        Mockito.doReturn(responseEntity).
                when(adminService).getAllUsers();

        mockMvc.perform(get("/api/admin/allusers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("lakshmi@example.com"));
    }

    // ========== Candidates ==========
    @Test
    void createCandidate_success() throws Exception {
        CandidateRequest req = CandidateRequest.builder()
                .fullName("John").email("john@example.com").build();

        ResponseEntity<?> mockResponse = new ResponseEntity<>("Candidate added successfully", HttpStatus.CREATED);

        Mockito.doReturn(mockResponse).
                when(adminService).createCandidate(any());

        mockMvc.perform(post("/api/admin/candidate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Candidate added successfully"));
    }

    @Test
    void getCandidate_success() throws Exception {
        ResponseEntity<List<Candidate>> responseEntity = ResponseEntity.ok(
                List.of(
                        Candidate.builder()
                                .id(1L).fullName("John").email("john@example.com").phone("12345")
                                .location("Bangalore").build()
                )
        );

        Mockito.doReturn(responseEntity).
                when(adminService).getCandidate();

        mockMvc.perform(get("/api/admin/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("John"));
    }

    // ========== Interviews ==========
    @Test
    void scheduleInterview_success() throws Exception {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        req.setCandidateId(1L);
        req.setScheduledTime(OffsetDateTime.parse("2025-09-08T10:00:00+05:30"));

        InterviewResponse resp = InterviewResponse.builder()
                .id(99L).candidateId(1L).status("TECH_SCHEDULED")
                .scheduledTime(req.getScheduledTime()).build();
        when(adminService.scheduleInterview(any())).thenReturn(resp);

        mockMvc.perform(post("/api/admin/interview/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("TECH_SCHEDULED"));
    }

    @Test
    void updateInterview_badRequest() throws Exception {
        UpdateInterviewRequest req = new UpdateInterviewRequest();
        req.setStatus("INVALID");

        when(adminService.updateInterview(Mockito.eq(1L), any())).thenReturn(null);

        mockMvc.perform(put("/api/admin/interview/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid interview update (status/time/assignee)"));
    }
}
