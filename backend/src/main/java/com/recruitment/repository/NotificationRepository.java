package com.recruitment.repository;

import com.recruitment.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
   List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

   List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

   long countByUserIdAndReadFalse(Long userId);
}
