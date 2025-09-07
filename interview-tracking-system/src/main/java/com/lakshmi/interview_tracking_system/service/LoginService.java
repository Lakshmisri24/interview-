package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.Role;
import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.dto.LoginDto;
import com.lakshmi.interview_tracking_system.repository.RoleRepository;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    public LoginDto login(UserDetails principal){

        User u = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        Role role = roleRepo.findById(u.getRoleId()).orElseThrow();
        LoginDto dto =new LoginDto();
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setRole(role.getName());
        return dto;
    }
}

