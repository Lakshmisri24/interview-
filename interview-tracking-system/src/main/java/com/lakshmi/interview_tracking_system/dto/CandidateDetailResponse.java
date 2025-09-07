package com.lakshmi.interview_tracking_system.dto;

import com.lakshmi.interview_tracking_system.dao.Candidate;
import com.lakshmi.interview_tracking_system.dao.Interview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDetailResponse {
    private Candidate candidate;     // never null
    private Interview interview;

}
