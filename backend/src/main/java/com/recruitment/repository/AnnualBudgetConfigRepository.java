package com.recruitment.repository;

import com.recruitment.entity.AnnualBudgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnualBudgetConfigRepository extends JpaRepository<AnnualBudgetConfig, Long> {

    /**
     * 查询某年度某单位的编制预算
     */
    Optional<AnnualBudgetConfig> findByConfigYearAndOrgUnitId(Integer configYear, Long orgUnitId);
}
