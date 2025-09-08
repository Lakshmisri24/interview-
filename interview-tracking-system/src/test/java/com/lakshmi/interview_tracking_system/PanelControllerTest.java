package com.lakshmi.interview_tracking_system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakshmi.interview_tracking_system.controller.PanelController;
import com.lakshmi.interview_tracking_system.dto.Rating;
import com.lakshmi.interview_tracking_system.service.PanelService;
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

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PanelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PanelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PanelService panelService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Principal mockPrincipal = () -> "testUser";

    // ==== GET /api/panel/interviews ====
    @Test
    void getInterviews_success() throws Exception {
        ResponseEntity<?> mockResponse = ResponseEntity.ok(
                List.of("Interview1", "Interview2")
        );

        Mockito.doReturn(mockResponse).when(panelService).getInterviews(any(Principal.class));

        mockMvc.perform(get("/api/panel/interviews").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Interview1"))
                .andExpect(jsonPath("$[1]").value("Interview2"));
    }

    // ==== GET /api/panel/candidate/{id} ====
    @Test
    void getCandidate_success() throws Exception {
        ResponseEntity<?> mockResponse = ResponseEntity.ok("Candidate Details");

        Mockito.doReturn(mockResponse).when(panelService).getCandidate(1L);

        mockMvc.perform(get("/api/panel/candidate/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Candidate Details"));
    }

    // ==== POST /api/panel/interview/{id} ====
    @Test
    void submitRating_success() throws Exception {
        Rating rating = new Rating();
        rating.setRating(5);

        ResponseEntity<?> mockResponse = ResponseEntity.status(HttpStatus.CREATED)
                .body("Rating submitted");

        Mockito.doReturn(mockResponse).when(panelService)
                .submitRating(eq(rating), eq(1L), any(Principal.class));

        mockMvc.perform(post("/api/panel/interview/1")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Rating submitted"));
    }
}

