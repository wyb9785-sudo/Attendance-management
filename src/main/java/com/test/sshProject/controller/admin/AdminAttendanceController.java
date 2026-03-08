package com.test.sshProject.controller.admin;

import com.test.sshProject.dto.AttendanceRecordDTO;
import com.test.sshProject.dto.AttendanceStatsDTO;
import com.test.sshProject.entity.*;
import com.test.sshProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/admin/attendance")
public class AdminAttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AttendanceConfigService configService;
    @Autowired
    private DeptService deptService;

    // 按员工查询考勤记录，使用模糊查询
    @GetMapping("/employee")
    @ResponseBody
    public List<AttendanceRecordDTO> getEmployeeAttendance(
            @RequestParam String empName,
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceRecord> records = attendanceService.getAttendanceByEmpName(empName, year, month);
        return convertToDTOs(records);
    }

    // 按部门查询考勤记录
    @GetMapping("/dept")
    @ResponseBody
    public List<AttendanceRecordDTO> getDeptAttendance(
            @RequestParam String deptName,
            @RequestParam int year,
            @RequestParam int month) {
        Dept dept = deptService.getByName(deptName);
        if (dept == null) {
            return Collections.emptyList();
        }
        List<AttendanceRecord> records = attendanceService.getAttendanceByDept(dept.getDeptId(), year, month);
        return convertToDTOs(records);
    }

    // 考勤统计数据
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getAttendanceStats(
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) String deptName,
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceRecord> records;
        if (empName != null) {
            Employee employee = employeeService.getByEmpName(empName);
            records = employee != null ?
                    attendanceService.getAttendanceByEmployee(employee.getEmpId(), year, month) :
                    Collections.emptyList();
        } else if (deptName != null) {
            Dept dept = deptService.getByName(deptName);
            records = dept != null ?
                    attendanceService.getAttendanceByDept(dept.getDeptId(), year, month) :
                    Collections.emptyList();
        } else {
            records = attendanceService.getAttendanceByCompany(year, month);
        }

        AttendanceStats stats = attendanceService.calculateStatistics(records);
        Map<String, Object> result = new HashMap<>();
        result.put("records", convertToDTOs(records));
        result.put("stats", convertToStatsDTO(stats));

        return result;
    }
    // 获取今日考勤统计
    @GetMapping("/today-stats")
    @ResponseBody
    public Map<String, Integer> getTodayStats() {
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> records = attendanceService.getAttendanceByCompany(
                        today.getYear(),
                        today.getMonthValue()
                ).stream()
                .filter(r -> r.getRecordDate().toLocalDate().equals(today))
                .collect(Collectors.toList());

        AttendanceStats stats = attendanceService.calculateStatistics(records);
        Map<String, Integer> result = new HashMap<>();
        result.put("normal", stats.getNormalCount());
        result.put("late", stats.getLateCount());
        result.put("early", stats.getEarlyCount());
        result.put("absent", stats.getAbsentCount());
        return result;
    }

    // 配置考勤规则
    @PostMapping("/config")
    @ResponseBody
    public String saveAttendanceConfig(@RequestBody AttendanceConfig config) {
        configService.save(config);
        return "考勤规则配置成功";
    }

    // 获取所有部门
    @GetMapping("/departments")
    @ResponseBody
    public List<Dept> getAllDepartments() {
        return deptService.listAllDepts();
    }
    // 管理员签到
    @PostMapping("/sign-in")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adminSignIn(HttpSession session) {
        Employee admin = (Employee) session.getAttribute("user");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "管理员未登录"));
        }
        try {
            String result = attendanceService.signIn(admin.getEmpId());
            // 返回签到后的完整状态
            LocalDate today = LocalDate.now();
            AttendanceRecord record = attendanceService.getRecordByDate(
                    admin.getEmpId(),
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
    // 管理员签退
    @PostMapping("/sign-out")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> adminSignOut(HttpSession session) {
        Employee admin = (Employee) session.getAttribute("user");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "管理员未登录"));
        }

        try {
            String result = attendanceService.signOut(admin.getEmpId());
            // 返回签退后的完整状态
            LocalDate today = LocalDate.now();
            AttendanceRecord record = attendanceService.getRecordByDate(
                    admin.getEmpId(),
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
    // 获取今日考勤状态
    @GetMapping("/today")
    @ResponseBody
    public ResponseEntity<?> getTodayAttendance(HttpSession session) {
        Employee admin = (Employee) session.getAttribute("user");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "管理员未登录"));
        }

        try {
            LocalDate today = LocalDate.now();
            AttendanceRecord record = attendanceService.getRecordByDate(
                    admin.getEmpId(),
                    java.sql.Date.valueOf(today)
            );

            Map<String, Object> result = new HashMap<>();
            result.put("signInTime", record != null ? record.getSignInTime() : null);
            result.put("signOutTime", record != null ? record.getSignOutTime() : null);
            result.put("status", record != null ? record.getStatus() : null);
            result.put("hasSignedIn", record != null && record.getSignInTime() != null);
            result.put("hasSignedOut", record != null && record.getSignOutTime() != null);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 辅助方法：转换为DTO列表
    private List<AttendanceRecordDTO> convertToDTOs(List<AttendanceRecord> records) {
        return records.stream().map(record -> {
            Employee employee = employeeService.getById(record.getEmpId());
            String empName = employee != null ? employee.getEmpName() : "";
            String deptName = employee != null && employee.getDeptId() != null ?
                    deptService.getById(employee.getDeptId()).getDeptName() : "";
            return new AttendanceRecordDTO(record, empName, deptName);
        }).collect(Collectors.toList());
    }

    // 辅助方法：转换为统计DTO
    private AttendanceStatsDTO convertToStatsDTO(AttendanceStats stats) {
        AttendanceStatsDTO dto = new AttendanceStatsDTO();
        dto.setNormalCount(stats.getNormalCount());
        dto.setLateCount(stats.getLateCount());
        dto.setEarlyCount(stats.getEarlyCount());
        dto.setAbsentCount(stats.getAbsentCount());
        dto.setInsufficientHoursCount(stats.getInsufficientHoursCount());
        return dto;
    }
}