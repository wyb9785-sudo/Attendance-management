package com.test.sshProject.controller;

import com.test.sshProject.entity.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        Employee user = (Employee) session.getAttribute("user");
        if (user == null) {
            if (!request.getRequestURI().equals("/login")) {
                response.sendRedirect("/login");
            }
            return false;
        }
        String path = request.getRequestURI();
        // 管理员路径检查
        if (path.startsWith("/admin") && !"ROLE_MANAGER".equals(user.getRole())) {
            response.sendError(HttpStatus.FORBIDDEN.value());
            return false;
        }
        // 员工路径检查
        if (path.startsWith("/employee") && "ROLE_MANAGER".equals(user.getRole())) {
            if (!path.equals("/admin/attendance")) {
                response.sendRedirect("/admin/attendance");
            }
            return false;
        }
        return true;
    }
}