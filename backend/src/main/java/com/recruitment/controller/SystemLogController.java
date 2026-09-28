package com.recruitment.controller;

import com.recruitment.dto.UserDTO;
import com.recruitment.entity.SystemLog;
import com.recruitment.repository.SystemLogRepository;
import com.recruitment.util.PermissionUtil;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/system-logs"})
public class SystemLogController {
   @Autowired
   private SystemLogRepository systemLogRepository;

   @GetMapping
   public Map<String, Object> getLogs(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, HttpSession session) {
      Map<String, Object> result = new HashMap();

      try {
         UserDTO user = PermissionUtil.getCurrentUser(session);
         PermissionUtil.requireRole(user, "admin");
         Page<SystemLog> logPage = this.systemLogRepository.findAll(PageRequest.of(page - 1, size, Sort.by(Direction.DESC, new String[]{"createdAt"})));
         result.put("success", true);
         result.put("data", logPage.getContent());
         result.put("total", logPage.getTotalElements());
      } catch (Exception e) {
         result.put("success", false);
         result.put("message", e.getMessage());
      }

      return result;
   }
}
