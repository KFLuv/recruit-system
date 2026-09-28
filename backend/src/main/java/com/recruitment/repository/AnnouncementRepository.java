package com.recruitment.repository;

import com.recruitment.entity.Announcement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
   List<Announcement> findByPublishedTrueOrderByCreatedAtDesc();

   List<Announcement> findAllByOrderByCreatedAtDesc();
}
