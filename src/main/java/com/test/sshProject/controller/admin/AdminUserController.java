package com.test.sshProject.controller.admin;

//员工管理

import com.test.sshProject.entity.Dept;
import com.test.sshProject.entity.Employee;
import com.test.sshProject.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.test.sshProject.service.DeptService; // 引入 DeptService
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DeptService deptService;

    // 创建管理员/员工
    @ResponseBody
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Employee employee) {
        try {
            employeeService.createUser(employee);
            Map<String, String> response = new HashMap<>();
            response.put("message", "创建成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "创建失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取单个员工信息
    @GetMapping("/{empId}")
    @ResponseBody
    public Employee getUserById(@PathVariable String empId) {
        return employeeService.getById(empId);
    }

    // 删除用户
    @DeleteMapping("/delete/{empId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String empId) {
        try {
            employeeService.deleteUser(empId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取部门列表
    @GetMapping("/departments")
    @ResponseBody
    public List<Dept> getDepartments() {
        return deptService.listAllDepts();
    }

    // 查询所有员工
    @ResponseBody
    @GetMapping("/list")
    public List<Employee> listUsers() {
        return employeeService.getAllEmployees();
    }
    // 添加获取所有用户的接口
    @GetMapping("/users")
    @ResponseBody
    public List<Employee> getAllUsers() {
        return employeeService.getAllEmployees();
    }
    // 添加 PUT 请求处理方法
    @PutMapping("/{empId}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable String empId, @RequestBody Employee employee) {
        try {
            // 假设这里调用服务层方法更新用户信息
            employeeService.updateById(employee);
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户信息更新成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "用户信息更新失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //"ROLE_MANAGER", "ROLE_EMPLOYEE"
    @PostMapping("/assign-role")
    public ResponseEntity<Map<String, String>> assignRole(@RequestParam String empId,
                                                          @RequestParam String role) {
        try {
            Employee employee = employeeService.getById(empId);
            if (employee == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "员工不存在");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (!Arrays.asList("ROLE_MANAGER", "ROLE_EMPLOYEE").contains(role)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "无效的角色");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            employee.setRole(role);
            employeeService.updateById(employee);
            Map<String, String> response = new HashMap<>();
            response.put("message", "角色分配成功");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "角色分配失败: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}