package com.test.sshProject.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.test.sshProject.entity.Dept;
import com.test.sshProject.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmployeeService extends IService<Employee> {

    // 根据员工姓名查询员工
    Employee getByEmpName(String empName);


    // 根据部门ID获取部门信息
    Dept getDeptById(Integer deptId);

    // 获取部门信息
    Dept getDeptByName(String deptName);

    // 创建用户
    void createUser(Employee employee);

    // 删除用户
    void deleteUser(String empId);

    // 获取所有员工
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByDept(Integer deptId);

    @Transactional
    void changePassword(String empId, String oldPassword, String newPassword);
}