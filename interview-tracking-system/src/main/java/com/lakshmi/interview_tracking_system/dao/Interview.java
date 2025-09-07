package com.lakshmi.interview_tracking_system.dao;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "interviews")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="candidate_id", nullable=false)
    private Long candidateId;

    @Column(name="assigned_user_id")
    private Long assignedUserId;

    @Column(name="scheduled_time", nullable=false)
    private OffsetDateTime scheduledTime;

    @Column(length = 20)
    private String status;                  // TECH_SCHEDULED / HR_COMPLETED / etc.

    @Column(name="tech_rating")
    private Integer techRating;             // 1–5

    @Column(name="hr_rating")
    private Integer hrRating;               // 1–5
}

