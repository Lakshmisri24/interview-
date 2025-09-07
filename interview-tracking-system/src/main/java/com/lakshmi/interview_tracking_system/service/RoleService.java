package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.Role;
import com.lakshmi.interview_tracking_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
    public String get(){
        return "hello";
    }

    public Optional<Role> getById(Long id) {
        return roleRepository.findById(id);
    }
}
