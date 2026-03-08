package com.test.sshProject.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.test.sshProject.entity.Dept;
import com.test.sshProject.entity.Employee;
import com.test.sshProject.entity.LeaveApplication;
import lombok.Data;

import java.util.Date;

@Data
public class LeaveApplicationDTO {
    @TableId("leave_id") // 确保与数据库主键字段名一致
    private Integer leaveId;
    private String empId;
    private String empName;
    private String deptName;
    private String leaveType;
    private Date startDate;
    private String approverId;
    private Date endDate;
    private String reason;
    private String status;
    private String approverName;
    private Date approveTime;
    private String approveOpinion;
    private Date createTime;

}