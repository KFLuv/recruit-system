package com.recruitment.repository;

import com.recruitment.entity.ProcessApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessApprovalRepository extends JpaRepository<ProcessApproval, Long> {
    List<ProcessApproval> findByProcessTypeAndStatus(String processType, String status);
    List<ProcessApproval> findByProcessTypeOrderByCreatedAtDesc(String processType);
}
