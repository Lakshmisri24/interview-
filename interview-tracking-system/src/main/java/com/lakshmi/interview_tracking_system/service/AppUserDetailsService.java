package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.Role;
import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.repository.RoleRepository;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        Role role = roleRepo.findById(u.getRoleId())
                .orElseThrow();

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())   //  correct variable
                .password(u.getPassword())    // bcrypt hash from DB
                .authorities(new SimpleGrantedAuthority("ROLE_" + role.getName())) //  fixed
                .build();
    }
}
