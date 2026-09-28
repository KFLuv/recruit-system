package com.recruitment.repository;

import com.recruitment.entity.PlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanVersionRepository extends JpaRepository<PlanVersion, Long> {

    List<PlanVersion> findByPlanIdOrderByVersionNoDesc(Long planId);

    int countByPlanId(Long planId);
}
