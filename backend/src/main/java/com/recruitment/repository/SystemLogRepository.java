package com.recruitment.repository;

import com.recruitment.entity.SystemLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
   List<SystemLog> findByUsername(String username);

   List<SystemLog> findByModule(String module);

   List<SystemLog> findAllByOrderByCreatedAtDesc();
}
