package com.recruitment.controller;

import com.recruitment.util.PermissionUtil;
import com.recruitment.dto.UserDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileDownloadController {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    @GetMapping("/resume/{filename}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String filename, HttpSession session) {
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");

            Path filePath = Paths.get(uploadDir + "/resume/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = filename.substring(filename.lastIndexOf("/") + 1);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "文件不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/work/{filename}")
    public ResponseEntity<Resource> downloadWorkAttachment(@PathVariable String filename, HttpSession session) {
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");

            Path filePath = Paths.get(uploadDir + "/work/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = filename.substring(filename.lastIndexOf("/") + 1);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/project/{filename}")
    public ResponseEntity<Resource> downloadProjectAttachment(@PathVariable String filename, HttpSession session) {
        try {
            UserDTO user = PermissionUtil.getCurrentUser(session);
            PermissionUtil.requireRole(user, "admin", "manager");

            Path filePath = Paths.get(uploadDir + "/project/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = filename.substring(filename.lastIndexOf("/") + 1);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
