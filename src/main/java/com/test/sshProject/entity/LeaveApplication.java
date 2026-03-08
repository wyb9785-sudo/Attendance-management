package com.test.sshProject.entity;
//请假申请
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@TableName("leave_application")
public class LeaveApplication {
    @TableId("leave_id") // 确保与数据库主键字段名一致
    private Integer leaveId;
    private String empId;
    private String leaveType;
    // 请假类型（事假/病假/公假）
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    // 开始日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String approverId;

    // 结束日期
    private String reason;
    // 请假事由
    private String status;
    // 状态（待审批/已通过/已驳回）
    // 审批人编号
    private Timestamp approveTime;
    // 审批时间
    private String approveOpinion;
    // 审批意见
    private Timestamp createTime;
    // 创建时间

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }


    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setApproveTime(Timestamp approveTime) {
        this.approveTime = approveTime;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public Integer getLeaveId() {
        return leaveId;
    }

    public String getEmpId() {
        return empId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getApproverId() {
        return approverId;
    }

    public Timestamp getApproveTime() {
        return approveTime;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }


}
