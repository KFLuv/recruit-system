package com.recruitment.controller;

import com.recruitment.dto.UserDTO;
import com.recruitment.entity.User;
import com.recruitment.service.UserService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            UserDTO user = userService.login(username, password);
            
            // 将用户信息存入 session
            session.setAttribute("user", user);
            session.setAttribute("username", username);
            
            result.put("success", true);
            result.put("data", user);
            result.put("message", "登录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");
            String realName = params.get("realName");
            String role = params.get("role");

            UserDTO created = userService.register(username, password, realName, role);
            result.put("success", true);
            result.put("data", created);
            result.put("message", "注册成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserDTO> users = userService.getAllUsers();
            result.put("success", true);
            result.put("data", users);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO user = userService.getUserById(id);
            result.put("success", true);
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping
    public Map<String, Object> createUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO created = userService.createUser(user);
            result.put("success", true);
            result.put("data", created);
            result.put("message", "创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserDTO updated = userService.updateUser(id, user);
            result.put("success", true);
            result.put("data", updated);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.deleteUser(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
