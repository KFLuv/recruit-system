package com.recruitment.repository;

import com.recruitment.entity.RecruitPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitPlanRepository extends JpaRepository<RecruitPlan, Long> {

    List<RecruitPlan> findByPlanYear(Integer planYear);

    List<RecruitPlan> findByOrgUnitId(Long orgUnitId);

    List<RecruitPlan> findByStatus(String status);

    /**
     * 查询某单位某年度已审批/在途的需求总人数 —— 编制预算校验用
     */
    @Query("SELECT COALESCE(SUM(r.totalCandidates), 0) FROM RecruitPlan r " +
           "WHERE r.orgUnitId = :orgUnitId AND r.planYear = :year AND r.status = :status")
    Integer sumTotalCandidatesByOrgUnitAndYearAndStatus(
            @Param("orgUnitId") Long orgUnitId,
            @Param("year") Integer year,
            @Param("status") String status);

    /**
     * 查询某单位某年度的所有有效需求（用于历史数据对比）
     */
    @Query("SELECT r FROM RecruitPlan r " +
           "WHERE r.orgUnitId = :orgUnitId AND r.planYear = :year " +
           "AND r.status IN ('approved', 'submitting', 'draft')")
    List<RecruitPlan> findActiveByOrgUnitAndYear(
            @Param("orgUnitId") Long orgUnitId,
            @Param("year") Integer year);
}
