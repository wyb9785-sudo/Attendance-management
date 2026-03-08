package com.test.sshProject.service;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.Date;
import java.util.List;

import com.test.sshProject.entity.AttendanceRecord;
import com.test.sshProject.entity.AttendanceStats;

public interface AttendanceService extends IService<AttendanceRecord> {

    // 签到
    String signIn(String empId);

    // 签退
    String signOut(String empId);

    // 根据员工查询考勤记录
    List<AttendanceRecord> getAttendanceByEmployee(String empId, int year, int month);
    // 根据员工姓名模糊查询考勤记录
    List<AttendanceRecord> getAttendanceByEmpName(String empName, int year, int month);
    // 根据部门查询考勤记录
    List<AttendanceRecord> getAttendanceByDept(int deptId, int year, int month);

    // 查询公司所有考勤记录
    List<AttendanceRecord> getAttendanceByCompany(int year, int month);

    // 计算考勤统计数据
    AttendanceStats calculateStatistics(List<AttendanceRecord> records);
    // 根据日期查询考勤记录
    AttendanceRecord getRecordByDate(String empId, Date recordDate);
}
