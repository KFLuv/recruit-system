package com.recruitment.controller;

import com.recruitment.dto.UserDTO;
import com.recruitment.entity.Notification;
import com.recruitment.repository.NotificationRepository;
import com.recruitment.util.PermissionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/notifications"})
public class NotificationController {
   @Autowired
   private NotificationRepository notificationRepository;

   @GetMapping
   public Map<String, Object> getNotifications(HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         List<Notification> list = this.notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
         result.put("success", true);
         result.put("data", list);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/unread-count"})
   public Map<String, Object> getUnreadCount(HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         long count = this.notificationRepository.countByUserIdAndReadFalse(user.getId());
         result.put("success", true);
         result.put("data", count);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PutMapping({"/{id}/read"})
   public Map<String, Object> markAsRead(@PathVariable Long id, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         Notification n = this.notificationRepository.findById(id).orElse(null);
         if (n != null) {
            n.setRead(true);
            this.notificationRepository.save(n);
         }

         result.put("success", true);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PutMapping({"/read-all"})
   public Map<String, Object> markAllAsRead(HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         List<Notification> unread = this.notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(user.getId());
         unread.forEach((n) -> n.setRead(true));
         this.notificationRepository.saveAll(unread);
         result.put("success", true);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
