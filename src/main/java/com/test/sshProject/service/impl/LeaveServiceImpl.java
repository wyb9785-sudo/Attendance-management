package com.test.sshProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.test.sshProject.dto.LeaveApplicationDTO;
import com.test.sshProject.entity.LeaveApplication;
import com.test.sshProject.mapper.LeaveMapper;
import com.test.sshProject.service.LeaveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, LeaveApplication> implements LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Override
    public int updateApproveId(Integer id, Integer approverId) {
        return leaveMapper.updateApproveId(id, approverId);
    }
    @Override
    @Transactional
    public void applyLeave(LeaveApplication leave) {
        // 1. 验证请假时间合理性
        if (leave.getStartDate().after(leave.getEndDate())) {
            throw new RuntimeException("请假开始时间不能晚于结束时间");
        }

        // 2. 设置默认状态和创建时间
        leave.setStatus("待审批");
        leave.setCreateTime(new Timestamp(System.currentTimeMillis()));

        // 3. 保存请假申请
        leaveMapper.insert(leave);
    }

    @Override
    @Transactional
    public void cancelLeave(Integer leaveId) {
        LeaveApplication leave = leaveMapper.selectById(leaveId);
        if (leave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        // 只能取消待审批状态的请假
        if (!"待审批".equals(leave.getStatus())) {
            throw new RuntimeException("只有待审批状态的请假可以取消");
        }

        leaveMapper.deleteById(leaveId);
    }

    @Override
    public List<LeaveApplication> getLeaveApplicationsByEmployee(String empId) {
        return leaveMapper.getByEmpId(empId);
    }

    @Override
    public List<LeaveApplication> getAllLeaveApplications() {
        return leaveMapper.selectList(null);
    }

    @Override
    @Transactional
    public void approveLeave(Integer leaveId, String status, String approveOpinion) {
        // 创建更新对象，只设置需要更新的字段
        LeaveApplication updateEntity = new LeaveApplication();
        updateEntity.setLeaveId(leaveId);
        updateEntity.setStatus(status);
        updateEntity.setApproveOpinion(approveOpinion);
        updateEntity.setApproveTime(new Timestamp(System.currentTimeMillis()));

        // 使用lambdaUpdate进行部分更新
        boolean updated = lambdaUpdate()
                .eq(LeaveApplication::getLeaveId, leaveId)
                .set(LeaveApplication::getStatus, status)
                .set(approveOpinion != null, LeaveApplication::getApproveOpinion, approveOpinion)
                .set(LeaveApplication::getApproveTime, new Timestamp(System.currentTimeMillis()))
                .update();

        if (!updated) {
            throw new RuntimeException("更新请假状态失败");
        }
    }

    @Override
    @Transactional
    public void approveLeave(LeaveApplication leave) {
        LeaveApplication existingLeave = leaveMapper.selectById(leave.getLeaveId());
        if (existingLeave == null) {
            throw new RuntimeException("请假记录不存在");
        }

        if (!"待审批".equals(existingLeave.getStatus())) {
            throw new RuntimeException("只有待审批状态的请假可以审批");
        }

        existingLeave.setStatus(leave.getStatus());
        existingLeave.setApproverId(leave.getApproverId());
        existingLeave.setApproveTime(new Timestamp(System.currentTimeMillis()));
        existingLeave.setApproveOpinion(leave.getApproveOpinion());

        leaveMapper.updateById(existingLeave);

    }
    @Override
    public List<LeaveApplication> getPendingLeaveApplications() {
        return leaveMapper.selectList(new QueryWrapper<LeaveApplication>()
                .eq("status", "待审批")
                .orderByAsc("create_time"));
    }

    @Override
    public List<LeaveApplication> getPendingLeaveApplicationsByType(String type) {
        QueryWrapper<LeaveApplication> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "待审批");
        if (!"all".equals(type)) {
            wrapper.eq("leave_type", type);
        }
        return leaveMapper.selectList(wrapper);
    }

    @Override
    public List<LeaveApplication> getAllLeaveApplicationsByStatusAndType(String status, String type) {
        QueryWrapper<LeaveApplication> wrapper = new QueryWrapper<>();
        if (!"all".equals(status)) {
            wrapper.eq("status", status);
        }
        if (!"all".equals(type)) {
            wrapper.eq("leave_type", type);
        }
        return leaveMapper.selectList(wrapper);
    }

    @Override
    public List<LeaveApplicationDTO> getAllLeaveApplicationsWithDetails() {
        return leaveMapper.getAllLeaveApplicationsWithDetails();
    }

    @Override
    public List<LeaveApplicationDTO> getPendingLeaveApplicationsWithDetails() {
        return leaveMapper.getPendingLeaveApplicationsWithDetails();
    }

    @Override
    public List<LeaveApplicationDTO> getAllLeaveApplicationsByStatusAndTypeWithDetails(String status, String type) {
        return leaveMapper.getAllLeaveApplicationsByStatusAndTypeWithDetails(status, type);
    }
    @Override
    public LeaveApplicationDTO getLeaveDetails(String leaveId) {
        return leaveMapper.getLeaveDetails(leaveId);
    }


}