package com.recruitment.repository;

import com.recruitment.entity.RecruitPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitPositionRepository extends JpaRepository<RecruitPosition, Long> {
    List<RecruitPosition> findByPlanId(Long planId);
}
