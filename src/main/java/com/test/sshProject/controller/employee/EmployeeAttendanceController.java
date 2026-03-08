package com.test.sshProject.controller.employee;


import com.test.sshProject.entity.*;
import com.test.sshProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/employee/attendance")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})

public class EmployeeAttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private EmployeeService employeeService;

    // 签到
    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> signIn(HttpSession session) {
        Employee employee = (Employee) session.getAttribute("user");
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用户未登录"));
        }

        try {
            String result = attendanceService.signIn(employee.getEmpId());
            // 返回签到后的完整状态
            LocalDate today = LocalDate.now();
            AttendanceRecord record = attendanceService.getRecordByDate(
                    employee.getEmpId(),
                    java.sql.Date.valueOf(today)
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", result);
            response.put("signInTime", record != null ? record.getSignInTime() : null);
            response.put("signOutTime", record != null ? record.getSignOutTime() : null);
            response.put("status", record != null ? record.getStatus() : null);
            response.put("hasSignedIn", record != null && record.getSignInTime() != null);
            response.put("hasSignedOut", record != null && record.getSignOutTime() != null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 签退
    @PostMapping("/sign-out")
    @ResponseBody
    public ResponseEntity<Map<String, String>> signOut(HttpSession session) {
        Employee employee = (Employee) session.getAttribute("user");
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "用户未登录"));
        }
        try {
            String result = attendanceService.signOut(employee.getEmpId()); // 可能未处理业务逻辑
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //今日考勤查询
    @GetMapping("/today")
    @ResponseBody
    public ResponseEntity<?> getTodayAttendance(
            @RequestParam String date,
            HttpSession session
    ) {
        Employee employee = (Employee) session.getAttribute("user");
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "用户未登录"));
        }
        try {
            LocalDate today = LocalDate.parse(date);
            AttendanceRecord record = attendanceService.getRecordByDate(
                    employee.getEmpId(),
                    java.sql.Date.valueOf(today)
            );
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 查看个人考勤明细
    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<?> getAttendanceDetail(
            @RequestParam(required = false, defaultValue = "0") int year,
            @RequestParam(required = false, defaultValue = "0") int month,
            HttpSession session
    ) {
        Employee employee = (Employee) session.getAttribute("user");
        if(employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "用户未登录"));
        }

        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        try {
            List<AttendanceRecord> records = attendanceService.getAttendanceByEmployee(
                    employee.getEmpId(),
                    year,
                    month
            );

            // 转换为前端需要的格式
            List<Map<String, Object>> result = records.stream()
                    .map(record -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("recordId", record.getRecordId());
                        map.put("empId", record.getEmpId());
                        map.put("recordDate", record.getRecordDate().toString());
                        map.put("signInTime", record.getSignInTime() != null ? record.getSignInTime().toString() : null);
                        map.put("signOutTime", record.getSignOutTime() != null ? record.getSignOutTime().toString() : null);
                        map.put("workHours", record.getWorkHours());
                        map.put("status", record.getStatus());
                        map.put("isWorkingDay", record.getIsWorkingDay());
                        map.put("remark", record.getRemark());
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 查看考勤统计
    @GetMapping("/statistics")
    public AttendanceStats getAttendanceStatistics(HttpSession session) {
        Employee employee = (Employee) session.getAttribute("user");
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        List<AttendanceRecord> records = attendanceService.getAttendanceByEmployee(employee.getEmpId(), year, month);
        return attendanceService.calculateStatistics(records);
    }
}
