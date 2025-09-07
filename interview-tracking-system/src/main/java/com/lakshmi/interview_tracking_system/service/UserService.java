package com.lakshmi.interview_tracking_system.service;

import com.lakshmi.interview_tracking_system.dao.User;
import com.lakshmi.interview_tracking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public List<User> user(){
        return userRepository.findAll();
    }
    public Optional<User> userById(Long id){
        return userRepository.findById(id);
    }
}
