package com.recruitment.controller;

import com.recruitment.annotation.Log;
import com.recruitment.dto.UserDTO;
import com.recruitment.entity.Announcement;
import com.recruitment.repository.AnnouncementRepository;
import com.recruitment.util.PermissionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/announcements"})
public class AnnouncementController {
   @Autowired
   private AnnouncementRepository announcementRepository;

   @GetMapping
   public Map<String, Object> getAnnouncements(HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         List<Announcement> list;
         if ("admin".equals(user.getRole())) {
            list = this.announcementRepository.findAllByOrderByCreatedAtDesc();
         } else {
            list = this.announcementRepository.findByPublishedTrueOrderByCreatedAtDesc();
         }

         result.put("success", true);
         result.put("data", list);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @GetMapping({"/published"})
   public Map<String, Object> getPublished() {
      Map<String, Object> result = new HashMap();

      try {
         List<Announcement> list = this.announcementRepository.findByPublishedTrueOrderByCreatedAtDesc();
         result.put("success", true);
         result.put("data", list);
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PostMapping
   @Log(
      module = "系统公告",
      description = "发布公告"
   )
   public Map<String, Object> createAnnouncement(@RequestBody Announcement announcement, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin");
         announcement.setAuthor(user.getRealName());
         Announcement saved = (Announcement)this.announcementRepository.save(announcement);
         result.put("success", true);
         result.put("data", saved);
         result.put("message", "公告创建成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @PutMapping({"/{id}"})
   @Log(
      module = "系统公告",
      description = "更新公告"
   )
   public Map<String, Object> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin");
         Announcement existing = (Announcement)this.announcementRepository.findById(id).orElseThrow(() -> new RuntimeException("公告不存在"));
         existing.setTitle(announcement.getTitle());
         existing.setContent(announcement.getContent());
         existing.setPublished(announcement.getPublished());
         this.announcementRepository.save(existing);
         result.put("success", true);
         result.put("message", "更新成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }

   @DeleteMapping({"/{id}"})
   @Log(
      module = "系统公告",
      description = "删除公告"
   )
   public Map<String, Object> deleteAnnouncement(@PathVariable Long id, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin");
         this.announcementRepository.deleteById(id);
         result.put("success", true);
         result.put("message", "删除成功");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
