package com.test.sshProject.entity;

//考勤配置

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@TableName("attendance_config")
public class AttendanceConfig {

    @TableId
    private Integer configId;
    private Integer deptId;
    private Time workStartTime;
    private Time workEndTime;
    private Double normalWorkHours;
    private Integer lateThreshold;
    private Integer earlyThreshold;
    private java.sql.Timestamp createTime;

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public void setWorkStartTime(Time workStartTime) {
        this.workStartTime = workStartTime;
    }

    public void setWorkEndTime(Time workEndTime) {
        this.workEndTime = workEndTime;
    }

    public void setNormalWorkHours(Double normalWorkHours) {
        this.normalWorkHours = normalWorkHours;
    }

    public void setLateThreshold(Integer lateThreshold) {
        this.lateThreshold = lateThreshold;
    }

    public void setEarlyThreshold(Integer earlyThreshold) {
        this.earlyThreshold = earlyThreshold;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getConfigId() {
        return configId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public Time getWorkStartTime() {
        return workStartTime;
    }

    public Time getWorkEndTime() {
        return workEndTime;
    }

    public Double getNormalWorkHours() {
        return normalWorkHours;
    }

    public Integer getLateThreshold() {
        return lateThreshold;
    }

    public Integer getEarlyThreshold() {
        return earlyThreshold;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }
}