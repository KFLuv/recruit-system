package com.recruitment.repository;

import com.recruitment.entity.ProcessApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessApprovalRecordRepository extends JpaRepository<ProcessApprovalRecord, Long> {
    List<ProcessApprovalRecord> findByApprovalId(Long approvalId);
}
