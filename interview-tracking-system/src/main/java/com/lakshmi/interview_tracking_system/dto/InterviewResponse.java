package com.lakshmi.interview_tracking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {

    private Long id;
    private Integer techRating;
    private Integer hrRating;
    private Long candidateId;
    private Long assignedUserId;      // null until assigned
    private OffsetDateTime scheduledTime;
    private String status;
}
