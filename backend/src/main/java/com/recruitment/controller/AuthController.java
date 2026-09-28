package com.recruitment.controller;

import com.recruitment.dto.UserDTO;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/check")
    public Map<String, Object> checkLogin(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        UserDTO user = (UserDTO) session.getAttribute("user");
        if (user != null) {
            result.put("success", true);
            result.put("data", user);
        } else {
            result.put("success", false);
            result.put("message", "未登录");
        }
        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        session.invalidate();
        result.put("success", true);
        result.put("message", "已退出登录");
        return result;
    }
}
