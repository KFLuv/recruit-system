package com.recruitment.repository;

import com.recruitment.entity.FileVersion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {
   List<FileVersion> findByBizTypeAndBizIdOrderByVersionDesc(String bizType, Long bizId);
}
