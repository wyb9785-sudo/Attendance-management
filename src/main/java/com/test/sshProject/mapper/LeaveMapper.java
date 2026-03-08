package com.test.sshProject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.sshProject.dto.LeaveApplicationDTO;
import com.test.sshProject.entity.LeaveApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveMapper extends BaseMapper<LeaveApplication> {
    @Select("SELECT * FROM leave_application WHERE emp_id = #{empId} " +
            "ORDER BY create_time DESC")
    List<LeaveApplication> getByEmpId(@Param("empId") String empId);

    @Select("SELECT * FROM leave_application WHERE emp_id = #{empId} " +
            "AND status = #{status} ORDER BY create_time DESC")
    List<LeaveApplication> getByEmpIdAndStatus(
            @Param("empId") String empId,
            @Param("status") String status
    );

    // 关联查询所有请假申请，包含员工和部门信息
    @Select("SELECT la.leave_id, la.emp_id, e.emp_name, d.dept_name, la.leave_type, la.start_date, la.end_date, la.reason, la.status, a.emp_name AS approver_name, la.approve_time, la.approve_opinion, la.create_time " +
            "FROM leave_application la " +
            "JOIN employee e ON la.emp_id = e.emp_id " +
            "JOIN dept d ON e.dept_id = d.dept_id " +
            "LEFT JOIN employee a ON la.approver_id = a.emp_id")

    List<LeaveApplicationDTO> getAllLeaveApplicationsWithDetails();

    // 关联查询待审批请假申请，包含员工和部门信息
    @Select("SELECT la.leave_id, la.emp_id, e.emp_name, d.dept_name, la.leave_type, la.start_date, la.end_date, la.reason, la.status, a.emp_name AS approver_name, la.approve_time, la.approve_opinion, la.create_time " +
            "FROM leave_application la " +
            "JOIN employee e ON la.emp_id = e.emp_id " +
            "JOIN dept d ON e.dept_id = d.dept_id " +
            "LEFT JOIN employee a ON la.approver_id = a.emp_id " +
            "WHERE la.status = '待审批'")
    List<LeaveApplicationDTO> getPendingLeaveApplicationsWithDetails();
    int updateApproveId(@Param("id") Integer id, @Param("approveId") Integer approveId);
    // 根据状态和类型关联查询请假申请，包含员工和部门信息
    @Select("SELECT la.leave_id, la.emp_id, e.emp_name, d.dept_name, la.leave_type, la.start_date, la.end_date, la.reason, la.status, a.emp_name AS approver_name, la.approve_time, la.approve_opinion, la.create_time " +
            "FROM leave_application la " +
            "JOIN employee e ON la.emp_id = e.emp_id " +
            "JOIN dept d ON e.dept_id = d.dept_id " +
            "LEFT JOIN employee a ON la.approver_id = a.emp_id " +
            "WHERE la.status = #{status} AND la.leave_type = #{type}")
    List<LeaveApplicationDTO> getAllLeaveApplicationsByStatusAndTypeWithDetails(@Param("status") String status, @Param("type") String type);

    @Select("SELECT la.leave_id, la.emp_id, e.emp_name, d.dept_name, la.leave_type, " +
            "la.start_date, la.end_date, la.reason, la.status, " +
            "a.emp_name AS approver_name, la.approve_time, la.approve_opinion, la.create_time " +
            "FROM leave_application la " +
            "JOIN employee e ON la.emp_id = e.emp_id " +
            "JOIN dept d ON e.dept_id = d.dept_id " +
            "LEFT JOIN employee a ON la.approver_id = a.emp_id " +
            "WHERE la.leave_id = #{leaveId}")
    LeaveApplicationDTO getLeaveDetails(@Param("leaveId") String leaveId);
}