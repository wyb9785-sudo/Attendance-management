package com.test.sshProject.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.sshProject.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    // 关联查询员工信息和部门名称
    @Select("SELECT e.emp_id, e.emp_name, e.password, e.dept_id, d.dept_name, e.position, e.hire_date, e.role, e.status " +
            "FROM employee e " +
            "LEFT JOIN dept d ON e.dept_id = d.dept_id " +
            "ORDER BY e.dept_id ASC, e.emp_name ASC")
    List<Employee> getAllEmployeesWithDeptName();
    // 根据部门ID获取员工数量
    Integer countByDeptId(@Param("deptId") Integer deptId);

}
