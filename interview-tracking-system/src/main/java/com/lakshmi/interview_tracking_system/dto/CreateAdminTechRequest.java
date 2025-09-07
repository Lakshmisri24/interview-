package com.lakshmi.interview_tracking_system.dto;

import lombok.Data;

@Data
public class CreateAdminTechRequest {
    private String fullName;
    private String email;
    private String password;
    private String roleName;
    private Boolean isActive;
}
