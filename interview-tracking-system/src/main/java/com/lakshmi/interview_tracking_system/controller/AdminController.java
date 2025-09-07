package com.lakshmi.interview_tracking_system.controller;

import com.lakshmi.interview_tracking_system.dto.*;
import com.lakshmi.interview_tracking_system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @PostMapping("/api/admin/users")
    public ResponseEntity <?> createAdminTech (@RequestBody CreateAdminTechRequest request){
        return adminService.createAdminTech(request);
    }
    @GetMapping("/api/admin/allusers")
    public ResponseEntity <?> getAllUsers(){
        return adminService.getAllUsers();
    }
    @DeleteMapping("api/admin/users/{id}")
    public ResponseEntity <?> deleteUser(@PathVariable Long id){

        return adminService.deleteUser(id);
    }

    @PostMapping("api/admin/candidate")

    public ResponseEntity <?> createCandidate (@RequestBody CandidateRequest request){
        return adminService.createCandidate(request);
    }

    @GetMapping("api/admin/candidates")
    public ResponseEntity <?> getCandidate (){
        return adminService.getCandidate();
    }
    @GetMapping("api/admin/candidate/{id}")
    public ResponseEntity<?> getCandidateWithInterview(@PathVariable Long id) {
        CandidateDetailResponse resp = adminService.getCandidateWithInterview(id);
        if (resp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidate not found");
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("api/admin/interview/schedule")
    public ResponseEntity<?> scheduleInterview(@RequestBody ScheduleInterviewRequest req) {
        InterviewResponse resp = adminService.scheduleInterview(req);
        if (resp == null) return ResponseEntity.badRequest().body("Invalid candidate/time or active interview exists");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
    @PutMapping("api/admin/interview/{id}")
    public ResponseEntity<?> updateInterview(@PathVariable Long id, @RequestBody UpdateInterviewRequest req) {
        InterviewResponse resp = adminService.updateInterview(id, req);
        if (resp == null) {
            return ResponseEntity.badRequest().body("Invalid interview update (status/time/assignee)");
        }
        return ResponseEntity.ok(resp);
    }

}
