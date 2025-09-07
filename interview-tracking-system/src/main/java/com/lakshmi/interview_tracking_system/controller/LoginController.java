package com.lakshmi.interview_tracking_system.controller;

import com.lakshmi.interview_tracking_system.dto.LoginDto;
import com.lakshmi.interview_tracking_system.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class LoginController {
    @Autowired
    private LoginService authService;
    @PostMapping("api/login")
    public ResponseEntity<LoginDto> login(@AuthenticationPrincipal UserDetails principal) {
        LoginDto dto = authService.login(principal);
        return ResponseEntity.ok(dto); // 200 OK
    }
}
