package com.lakshmi.interview_tracking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInterviewRequest {
    // optional
    private String status;                // optional: HR_SCHEDULED | SELECTED | CANCELLED | REJECTED
    private Long assignedUserId;
}
