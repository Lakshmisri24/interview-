package com.lakshmi.interview_tracking_system.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "candidates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 10)
    private String phone;

    @Column(name = "primary_skills", columnDefinition = "TEXT")
    private String primarySkills;

    @Column(name = "secondary_skills", columnDefinition = "TEXT")
    private String secondarySkills;

    // NUMERIC(4,1) → Double
    private Double experience;

    private String qualification;
    private String designation;

    @Column(name = "notice_period")
    private Integer noticePeriod;

    private String location;
}

