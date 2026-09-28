package com.recruitment.util;

import com.recruitment.dto.UserDTO;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Set;

public class PermissionUtil {

    /**
     * 从 session 获取当前登录用户
     */
    public static UserDTO getCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (UserDTO) session.getAttribute("user");
    }

    /**
     * 检查当前用户是否拥有指定角色之一
     */
    public static boolean hasRole(UserDTO user, String... roles) {
        if (user == null) return false;
        String userRole = user.getRole();
        if (userRole == null) return false;
        return Arrays.asList(roles).contains(userRole);
    }

    /**
     * 校验当前用户是否拥有指定角色之一，没有则抛出异常
     */
    public static void requireRole(UserDTO user, String... roles) {
        if (!hasRole(user, roles)) {
            throw new RuntimeException("权限不足：需要 " + String.join("/", roles) + " 角色");
        }
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin(UserDTO user) {
        return hasRole(user, "admin");
    }

    /**
     * 判断是否为管理员或经理
     */
    public static boolean isAdminOrManager(UserDTO user) {
        return hasRole(user, "admin", "manager");
    }
}
