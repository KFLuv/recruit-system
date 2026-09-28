package com.recruitment.controller;

import com.recruitment.util.PermissionUtil;
import com.recruitment.dto.UserDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    @PostMapping("/resume")
    public Map<String, Object> uploadResume(@RequestParam("file") MultipartFile file, HttpSession session) {
        return uploadFile(file, session, "resume");
    }

    @PostMapping("/work")
    public Map<String, Object> uploadWorkAttachment(@RequestParam("file") MultipartFile file, HttpSession session) {
        return uploadFile(file, session, "work");
    }

    @PostMapping("/project")
    public Map<String, Object> uploadProjectAttachment(@RequestParam("file") MultipartFile file, HttpSession session) {
        return uploadFile(file, session, "project");
    }

    private Map<String, Object> uploadFile(MultipartFile file, HttpSession session, String subDir) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");

            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件不能为空");
                return result;
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || !(originalName.endsWith(".pdf") || originalName.endsWith(".doc") || originalName.endsWith(".docx"))) {
                result.put("success", false);
                result.put("message", "仅支持 PDF、Word 格式文件");
                return result;
            }

            String ext = originalName.substring(originalName.lastIndexOf("."));
            String savedName = UUID.randomUUID().toString() + ext;

            File dir = new File(uploadDir + "/" + subDir);
            if (!dir.exists()) dir.mkdirs();

            Path filePath = Paths.get(dir.getAbsolutePath(), savedName);
            Files.write(filePath, file.getBytes());

            String accessPath = "/uploads/" + subDir + "/" + savedName;

            result.put("success", true);
            result.put("data", accessPath);
            result.put("originalName", originalName);
            result.put("message", "上传成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
