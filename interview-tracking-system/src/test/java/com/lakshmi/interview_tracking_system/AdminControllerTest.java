
package com.lakshmi.interview_tracking_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakshmi.interview_tracking_system.controller.AdminController;
import com.lakshmi.interview_tracking_system.dto.*;
import com.lakshmi.interview_tracking_system.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testCreateAdminTech() throws Exception {
        CreateAdminTechRequest request = new CreateAdminTechRequest();
        // set fields on request

        Mockito.when(adminService.createAdminTech(Mockito.any()))
                .thenReturn(ResponseEntity.ok().body(null));

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
//                .andExpect(content().string("User created"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        Mockito.when(adminService.getAllUsers())
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/api/admin/allusers"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.when(adminService.deleteUser(1L))
                .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isOk());
//                .andExpect(content().string("Deleted"));
    }

    @Test
    void testCreateCandidate() throws Exception {
        CandidateRequest request = new CandidateRequest();
        // set fields on request

        Mockito.when(adminService.createCandidate(Mockito.any()))
                .thenReturn(ResponseEntity.status(201).body(null));

        mockMvc.perform(post("/api/admin/candidate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
//                .andExpect(content().string("Candidate created"));
    }

    @Test
    void testGetCandidate() throws Exception {
        Mockito.when(adminService.getCandidate())
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/api/admin/candidates"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCandidateWithInterview_found() throws Exception {
        CandidateDetailResponse response = new CandidateDetailResponse();
        Mockito.when(adminService.getCandidateWithInterview(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/candidate/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCandidateWithInterview_notFound() throws Exception {
        Mockito.when(adminService.getCandidateWithInterview(999L))
                .thenReturn(null);

        mockMvc.perform(get("/api/admin/candidate/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Candidate not found"));
    }

    @Test
    void testScheduleInterview_valid() throws Exception {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();
        InterviewResponse resp = new InterviewResponse();

        Mockito.when(adminService.scheduleInterview(Mockito.any()))
                .thenReturn(resp);

        mockMvc.perform(post("/api/admin/interview/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void testScheduleInterview_invalid() throws Exception {
        ScheduleInterviewRequest req = new ScheduleInterviewRequest();

        Mockito.when(adminService.scheduleInterview(Mockito.any()))
                .thenReturn(null);

        mockMvc.perform(post("/api/admin/interview/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid candidate/time or active interview exists"));
    }

    @Test
    void testUpdateInterview_valid() throws Exception {
        UpdateInterviewRequest req = new UpdateInterviewRequest();
        InterviewResponse resp = new InterviewResponse();

        Mockito.when(adminService.updateInterview(Mockito.eq(1L), Mockito.any()))
                .thenReturn(resp);

        mockMvc.perform(put("/api/admin/interview/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateInterview_invalid() throws Exception {
        UpdateInterviewRequest req = new UpdateInterviewRequest();

        Mockito.when(adminService.updateInterview(Mockito.eq(1L), Mockito.any()))
                .thenReturn(null);

        mockMvc.perform(put("/api/admin/interview/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid interview update (status/time/assignee)"));
    }
}
