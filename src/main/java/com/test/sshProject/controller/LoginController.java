package com.test.sshProject.controller;

import com.test.sshProject.entity.Employee;
import com.test.sshProject.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private EmployeeService employeeService;
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login";
    }
    @PostMapping("/login")
    public String login(
            @RequestParam String empName,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        Employee employee = employeeService.getByEmpName(empName);
        if (employee != null && employee.getPassword().equals(password)) {
            session.setAttribute("user", employee);
            session.setAttribute("currentUserId", employee.getEmpId());
            if ("ROLE_MANAGER".equals(employee.getRole())) {
                return "redirect:/admin/attendance";
            } else {
                return "redirect:/employee/attendance";
            }
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "home";
    }
}