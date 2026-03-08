package com.test.sshProject.controller;

import com.test.sshProject.entity.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class PageController {

    @GetMapping("/admin/attendance")
    public String adminAttendance(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        if(!"ROLE_MANAGER".equals(user.getRole())) return "redirect:/employee/attendance";
        return "admin/attendance";
    }

    @GetMapping("/employee/attendance")
    public String employeeAttendance(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        if("ROLE_MANAGER".equals(user.getRole())) return "redirect:/admin/attendance";
        return "employee/attendance";
    }
    @GetMapping("/employee/leave")
    public String employeeLeavePage(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        return "employee/leave";
    }
    @GetMapping("/admin/departments")
    public String adminDepartments(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        if(!"ROLE_MANAGER".equals(user.getRole())) return "redirect:/employee/attendance";
        return "admin/departments";
    }

    @GetMapping("/admin/user")
    public String adminUserManagement(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        if(!"ROLE_MANAGER".equals(user.getRole())) return "redirect:/employee/attendance";
        return "admin/user";
    }

    @GetMapping("/admin/leave")
    public String adminLeaveManagement(HttpSession session) {
        Employee user = (Employee) session.getAttribute("user");
        if(user == null) return "redirect:/login";
        if(!"ROLE_MANAGER".equals(user.getRole())) return "redirect:/employee/attendance";
        return "admin/leave";
    }

}
