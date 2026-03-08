package com.test.sshProject.service.impl;

import com.test.sshProject.entity.Dept;
import com.test.sshProject.entity.Employee;
import com.test.sshProject.mapper.DeptMapper;
import com.test.sshProject.mapper.EmployeeMapper;
import com.test.sshProject.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private DeptMapper deptMapper;

    @Override
    public Employee getByEmpName(String empName) {
        return employeeMapper.selectOne(new QueryWrapper<Employee>()
                .eq("emp_name", empName));
    }
    @Override
    public Dept getDeptById(Integer deptId) {
        if (deptId == null) {
            return null;
        }
        return deptMapper.selectById(deptId);
    }

    @Override
    public Dept getDeptByName(String deptName) {
        return deptMapper.selectOne(new QueryWrapper<Dept>()
                .eq("dept_name", deptName));
    }

    @Override
    @Transactional
    public void createUser(Employee employee) {
        // 1. 检查用户名是否已存在
        if (employeeMapper.selectCount(new QueryWrapper<Employee>()
                .eq("emp_name", employee.getEmpName())) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        //2.检查id是否存在
        Employee existingEmployee = employeeMapper.selectById(employee.getEmpId());
        if (existingEmployee != null) {
            throw new RuntimeException("员工ID已存在: " + employee.getEmpId());
        }

        // 3. 设置默认角色(如果未指定)
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            employee.setRole("ROLE_EMPLOYEE");
        }

        // 4. 设置默认状态(1-启用)
        if (employee.getStatus() == null) {
            employee.setStatus(1);
        }

        // 5. 保存员工信息
        employeeMapper.insert(employee);
    }

    @Override
    @Transactional
    public void deleteUser(String empId) {
        // 1. 检查员工是否存在
        Employee employee = employeeMapper.selectById(empId);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        // 2. 执行删除
        employeeMapper.deleteById(empId);
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeMapper.selectList(new QueryWrapper<Employee>()
                .orderByAsc("dept_id", "emp_name"));
        for (Employee employee : employees) {
            Dept dept = deptMapper.selectById(employee.getDeptId());
            if (dept != null) {
                employee.setDeptName(dept.getDeptName());
            }
        }
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByDept(Integer deptId) {
        return employeeMapper.selectList(new QueryWrapper<Employee>()
                .eq("dept_id", deptId)
                .orderByAsc("emp_name"));
    }

    @Transactional
    @Override
    public void changePassword(String empId, String oldPassword, String newPassword) {
        Employee employee = employeeMapper.selectById(empId);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        employeeMapper.updateById(employee);
    }
//    @Override
//    public List<Dept> getAllDepts() {
//        return deptMapper.selectList(new QueryWrapper<Dept>());
//    }
}