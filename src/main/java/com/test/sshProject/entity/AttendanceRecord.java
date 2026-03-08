package com.test.sshProject.entity;
//考勤记录

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AttendanceRecord {
    private int recordId;
    private String empId;
    // 员工编号
    private Date recordDate;
    // 考勤日期
    private Time signInTime;
    // 签到时间
    private Time  signOutTime;
    // 签退时间
    private Double workHours;
    // 工作时长(小时)
    private String status;
    // 考勤状态（正常/迟到/早退/旷工）
    private Integer isWorkingDay;
    // 是否工作日：1-是，0-否
    private String remark;
    // 备注信息

    public int getRecordId() {
        return recordId;
    }

    public String getEmpId() {
        return empId;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public Time getSignInTime() {
        return signInTime;
    }

    public Time getSignOutTime() {
        return signOutTime;
    }

    public String getStatus() {
        return status;
    }

    public Integer getIsWorkingDay() {
        return isWorkingDay;
    }

    public Double getWorkHours() {
        return workHours;
    }

    public String getRemark() {
        return remark;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public void setSignInTime(Time signInTime) {
        this.signInTime = signInTime;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public void setSignOutTime(Time signOutTime) {
        this.signOutTime = signOutTime;
    }

    public void setWorkHours(Double workHours) {
        this.workHours = workHours;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIsWorkingDay(Integer isWorkingDay) {
        this.isWorkingDay = isWorkingDay;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}