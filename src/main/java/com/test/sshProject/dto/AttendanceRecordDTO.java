package com.test.sshProject.dto;

import com.test.sshProject.entity.AttendanceRecord;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class AttendanceRecordDTO {
    private int recordId;
    private String empId;
    private String empName;
    private String deptName;
    private Date recordDate;
    private Time signInTime;
    private Time signOutTime;
    private Double workHours;
    private String status;
    private Integer isWorkingDay;
    private String remark;

    // 从AttendanceRecord转换为DTO的构造方法
    public AttendanceRecordDTO(AttendanceRecord record, String empName, String deptName) {
        this.recordId = record.getRecordId();
        this.empId = record.getEmpId();
        this.recordDate = record.getRecordDate();
        this.signInTime = record.getSignInTime();
        this.signOutTime = record.getSignOutTime();
        this.workHours = record.getWorkHours();
        this.status = record.getStatus();
        this.isWorkingDay = record.getIsWorkingDay();
        this.remark = record.getRemark();
        this.empName = empName;
        this.deptName = deptName;
    }
}
