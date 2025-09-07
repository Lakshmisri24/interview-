package com.lakshmi.interview_tracking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateAdminTechResponse {
    private String fullName;
    private String email;
    private String password;
    private String roleName;
    private Boolean isActive;
    private long id;
}
