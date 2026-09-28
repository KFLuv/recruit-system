package com.recruitment.controller;

import com.recruitment.annotation.Log;
import com.recruitment.dto.UserDTO;
import com.recruitment.entity.FileVersion;
import com.recruitment.repository.FileVersionRepository;
import com.recruitment.util.PermissionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/file-versions"})
public class FileVersionController {
   @Autowired
   private FileVersionRepository fileVersionRepository;

   @GetMapping
   public Map<String, Object> getVersions(@RequestParam String bizType, @RequestParam Long bizId) {
      Map<String, Object> result = new HashMap();

      try {
         if (bizId == null) {
            result.put("success", false);
            result.put("message", "关联 ID 不能为空");
            return result;
         }

         List<FileVersion> list = this.fileVersionRepository.findByBizTypeAndBizIdOrderByVersionDesc(bizType, bizId);
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
      module = "文件版本",
      description = "上传新版本"
   )
   public Map<String, Object> addVersion(@RequestBody FileVersion fileVersion, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin", "manager");
         List<FileVersion> existing = this.fileVersionRepository.findByBizTypeAndBizIdOrderByVersionDesc(fileVersion.getBizType(), fileVersion.getBizId());
         int maxVersion = existing.isEmpty() ? 0 : ((FileVersion)existing.get(0)).getVersion();
         fileVersion.setVersion(maxVersion + 1);
         fileVersion.setUploader(user.getRealName());
         FileVersion saved = (FileVersion)this.fileVersionRepository.save(fileVersion);
         result.put("success", true);
         result.put("data", saved);
         result.put("message", "版本已记录");
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
