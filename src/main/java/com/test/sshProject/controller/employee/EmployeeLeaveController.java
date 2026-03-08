package com.test.sshProject.controller.employee;


import com.test.sshProject.entity.Employee;
import com.test.sshProject.entity.LeaveApplication;
import com.test.sshProject.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee/leave/")
public class EmployeeLeaveController {

    @Autowired
    private LeaveService leaveService;

    // 访问请假申请页面
    @GetMapping
    public String showLeavePage(HttpSession session) {
        Employee employee = (Employee) session.getAttribute("user");
        if (employee == null) {
            return "redirect:/login"; // 重定向到登录页面
        }
        return "leave"; // 返回 leave.html 页面
    }

    // 提交请假申请
    @PostMapping("/apply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyLeave(
            @RequestBody Map<String, String> leaveData,
            HttpSession session) {

        Employee employee = (Employee) session.getAttribute("user");
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户未登录"));
        }

        try {
            LeaveApplication leave = new LeaveApplication();
            leave.setEmpId(employee.getEmpId());
            leave.setLeaveType(leaveData.get("leaveType"));
            leave.setStartDate(Date.valueOf(leaveData.get("startDate")));
            leave.setEndDate(Date.valueOf(leaveData.get("endDate")));
            leave.setReason(leaveData.get("reason"));
            leave.setStatus("待审批");
            leave.setCreateTime(new Timestamp(System.currentTimeMillis()));

            leaveService.applyLeave(leave);

            // 添加日志确认插入的 leaveId
            System.out.println("插入的请假申请 leaveId: " + leave.getLeaveId());

            return ResponseEntity.ok(Map.of(
                    "message", "请假申请提交成功",
                    "leaveId", leave.getLeaveId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 查看个人请假记录
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<?> listMyLeaves(
            @RequestParam(required = false, defaultValue = "all") String status,
            HttpSession session) {

        Employee employee = (Employee) session.getAttribute("user");
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户未登录"));
        }

        try {
            List<LeaveApplication> leaves;
            if ("all".equals(status)) {
                leaves = leaveService.getLeaveApplicationsByEmployee(employee.getEmpId());
            } else {
                leaves = leaveService.getLeaveApplicationsByEmployee(employee.getEmpId())
                        .stream()
                        .filter(leave -> status.equals(leave.getStatus()))
                        .collect(Collectors.toList());
            }

            // 转换为DTO格式
            List<Map<String, Object>> result = leaves.stream()
                    .map(leave -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("leaveId", leave.getLeaveId());
                        map.put("leaveType", leave.getLeaveType());
                        map.put("startDate", leave.getStartDate().toString());
                        map.put("endDate", leave.getEndDate().toString());
                        map.put("reason", leave.getReason());
                        map.put("status", leave.getStatus());
                        map.put("approveOpinion", leave.getApproveOpinion());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 取消请假申请
    @DeleteMapping("/cancel/{leaveId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cancelLeave(
            @PathVariable Integer leaveId,
            HttpSession session) {

        Employee employee = (Employee) session.getAttribute("user");
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户未登录"));
        }

        try {
            leaveService.cancelLeave(leaveId);
            return ResponseEntity.ok(Map.of("message", "请假申请已取消"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}