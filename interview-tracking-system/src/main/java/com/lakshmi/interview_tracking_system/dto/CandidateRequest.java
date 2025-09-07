package com.lakshmi.interview_tracking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRequest {

    private String fullName;
    private String email;
    private String phone;
    private String primarySkills;
    private String secondarySkills;
    private Double experience;
    private String qualification;
    private String designation;
    private Integer noticePeriod;
    private String location;
}
