package com.test.sshProject.service;

import com.test.sshProject.dto.LeaveApplicationDTO;
import com.test.sshProject.entity.LeaveApplication;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface LeaveService extends IService<LeaveApplication> {

    // 员工提交请假申请
    void applyLeave(LeaveApplication leave);

    // 取消请假申请
    void cancelLeave(Integer leaveId);

    // 根据员工ID查询请假申请
    List<LeaveApplication> getLeaveApplicationsByEmployee(String empId);

    // 获取所有请假申请
    List<LeaveApplication> getAllLeaveApplications();

    // 审批请假申请
    void approveLeave(LeaveApplication leave);

    List<LeaveApplication> getPendingLeaveApplications();

    List<LeaveApplication> getPendingLeaveApplicationsByType(String type);

    List<LeaveApplication> getAllLeaveApplicationsByStatusAndType(String status, String type);
    //请假详细信息
    LeaveApplicationDTO getLeaveDetails(String leaveId);
    // 关联查询所有请假申请，包含员工和部门信息
    List<LeaveApplicationDTO> getAllLeaveApplicationsWithDetails();

    // 关联查询待审批请假申请，包含员工和部门信息
    List<LeaveApplicationDTO> getPendingLeaveApplicationsWithDetails();
    // 更新approveId
    int updateApproveId(Integer id, Integer approverId);
    // 根据状态和类型关联查询请假申请，包含员工和部门信息
    List<LeaveApplicationDTO> getAllLeaveApplicationsByStatusAndTypeWithDetails(String status, String type);
    void approveLeave(Integer leaveId, String status, String approveOpinion);
}