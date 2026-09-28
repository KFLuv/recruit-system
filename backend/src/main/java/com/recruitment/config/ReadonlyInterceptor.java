package com.recruitment.config;

import com.recruitment.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 只读账号拦截器：
 * readonly 角色的用户只能查看（GET），禁止一切写操作（POST/PUT/DELETE），
 * 仅放行 登录 / 注册 / 退出 等认证接口。
 */
@Component
public class ReadonlyInterceptor implements HandlerInterceptor {

    /** 只读账号可使用的写接口白名单（认证必须） */
    private static final String[] ALLOW_WRITE_PATHS = {
            "/api/users/login",
            "/api/users/register",
            "/api/auth/logout"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();

        // 只拦截 /api/ 接口
        if (uri == null || !uri.startsWith("/api/")) {
            return true;
        }

        // GET 查询一律放行
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 认证白名单放行
        for (String allowPath : ALLOW_WRITE_PATHS) {
            if (uri.equals(allowPath)) {
                return true;
            }
        }

        // 检查当前用户是否为只读账号
        HttpSession session = request.getSession(false);
        UserDTO user = session == null ? null : (UserDTO) session.getAttribute("user");
        if (user != null && "readonly".equals(user.getRole())) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write("{\"success\":false,\"message\":\"只读账号无操作权限，仅可查看\"}");
            return false;
        }
        return true;
    }
}
