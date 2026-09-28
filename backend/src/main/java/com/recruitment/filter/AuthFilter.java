package com.recruitment.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthFilter implements Filter {
    
    // 公开路径（无需认证）
    private static final Set<String> PUBLIC_PATHS = new HashSet<>();
    static {
        PUBLIC_PATHS.add("/api/users/login");
        PUBLIC_PATHS.add("/api/users/register");
        PUBLIC_PATHS.add("/api/auth/check");
        PUBLIC_PATHS.add("/api/auth/logout");
        PUBLIC_PATHS.add("/api/bug-reports");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestPath = httpRequest.getRequestURI();
        
        // 在所有响应中添加 CORS header
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        
        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        // 跳过公开路径
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 检查 session
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "请先登录");
            
            new ObjectMapper().writeValue(httpResponse.getOutputStream(), result);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}
