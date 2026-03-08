package com.test.sshProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.test.sshProject.entity.*;
import com.test.sshProject.mapper.*;
import com.test.sshProject.service.AttendanceService;
import com.test.sshProject.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, AttendanceRecord> implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AttendanceConfigMapper configMapper;


    @Override
    @Transactional
    public String signIn(String empId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();

            // 1. 检查是否已签到
            AttendanceRecord record = attendanceMapper.getRecordByDate(empId, DateUtils.asDate(today));
            if (record != null && record.getSignInTime() != null) {
                return "今日已签到，签到时间：" + record.getSignInTime();
            }

            // 2. 获取员工信息和部门考勤配置
            Employee employee = employeeMapper.selectById(empId);
            if (employee == null) {
                return "员工不存在";
            }

            AttendanceConfig config = configMapper.getByDeptId(employee.getDeptId());
            if (config == null) {
                return "部门考勤配置不存在";
            }

            // 3. 创建新记录
            record = new AttendanceRecord();
            record.setEmpId(empId);
            record.setRecordDate(DateUtils.asDate(today));
            record.setIsWorkingDay(DateUtils.isWorkDay(today) ? 1 : 0);
            record.setSignInTime(java.sql.Time.valueOf(now.toLocalTime()));

            // 计算是否迟到
            LocalTime workStartTime = config.getWorkStartTime().toLocalTime();
            if (now.toLocalTime().isAfter(workStartTime)) {
                long lateMinutes = ChronoUnit.MINUTES.between(workStartTime, now.toLocalTime());
                record.setStatus(lateMinutes > config.getLateThreshold() ? "迟到" : "正常");
            } else {
                record.setStatus("正常");
            }

            // 4. 保存记录
            this.save(record); // 使用MyBatis-Plus的save方法

            return "签到成功，时间：" + now.toLocalTime();
        } catch (Exception e) {
            log.error("签到失败", e);
            throw new RuntimeException("签到处理失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String signOut(String empId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        Date sqlDate = Date.valueOf(today);
        Time sqlTime = Time.valueOf(now.toLocalTime());

        // 获取今日考勤记录
        AttendanceRecord record = getRecordByDate(empId, sqlDate);
        if (record == null || record.getSignInTime() == null) {
            throw new RuntimeException("请先完成签到");
        }

        if (record.getSignOutTime() != null) {
            return "今日已签退，签退时间：" + record.getSignOutTime();
        }

        // 获取部门配置
        Employee employee = employeeMapper.selectById(empId);
        AttendanceConfig config = configMapper.getByDeptId(employee.getDeptId());

        // 设置签退时间
        record.setSignOutTime(sqlTime);

        // 计算工作时长
        long minutes = Duration.between(
                record.getSignInTime().toLocalTime(),
                now.toLocalTime()
        ).toMinutes();
        double workHours = minutes / 60.0;
        record.setWorkHours(workHours);

        // 判断考勤状态
        LocalTime workEndTime = config.getWorkEndTime().toLocalTime();
        if (now.toLocalTime().isBefore(workEndTime)) {
            long earlyMinutes = ChronoUnit.MINUTES.between(
                    now.toLocalTime(),
                    workEndTime
            );
            if (earlyMinutes > config.getEarlyThreshold()) {
                record.setStatus("早退");
            }
        }

        // 更新记录
        this.updateById(record);

        return "签退成功，时间：" + now.toLocalTime() +
                "，工作时长：" + String.format("%.1f", workHours) + "小时";
    }

    @Override
    public List<AttendanceRecord> getAttendanceByEmployee(String empId, int year, int month) {
        // 先查询该员工该月的所有考勤记录（包括未签到的）
        List<AttendanceRecord> records = attendanceMapper.getRecordsByEmployee(empId, year, month);

        // 获取该月的所有工作日
        List<LocalDate> workDays = DateUtils.getWorkDaysInMonth(year, month);

        // 确保每一天都有记录
        List<AttendanceRecord> result = new ArrayList<>();
        for (LocalDate day : workDays) {
            Date sqlDate = Date.valueOf(day);
            boolean found = false;

            for (AttendanceRecord record : records) {
                if (record.getRecordDate().equals(sqlDate)) {
                    result.add(record);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // 创建空的考勤记录
                AttendanceRecord emptyRecord = new AttendanceRecord();
                emptyRecord.setEmpId(empId);
                emptyRecord.setRecordDate(sqlDate);
                emptyRecord.setIsWorkingDay(1);
                emptyRecord.setStatus("未签到");
                result.add(emptyRecord);
            }
        }

        return result;
    }

    @Override
    public List<AttendanceRecord> getAttendanceByDept(int deptId, int year, int month) {
        return attendanceMapper.getRecordsByDept(deptId, year, month);
    }

    @Override
    public List<AttendanceRecord> getAttendanceByCompany(int year, int month) {
        return attendanceMapper.getRecordsByCompany(year, month);
    }

    @Override
    public AttendanceStats calculateStatistics(List<AttendanceRecord> records) {
        AttendanceStats stats = new AttendanceStats();

        records.stream()
                .filter(record -> record.getIsWorkingDay() == 1)
                .forEach(record -> {
                    switch (record.getStatus()) {
                        case "正常": stats.setNormalCount(stats.getNormalCount() + 1); break;
                        case "迟到": stats.setLateCount(stats.getLateCount() + 1); break;
                        case "早退": stats.setEarlyCount(stats.getEarlyCount() + 1); break;
                        case "缺勤": stats.setAbsentCount(stats.getAbsentCount() + 1); break;
                        case "工时不足": stats.setInsufficientHoursCount(stats.getInsufficientHoursCount() + 1); break;
                    }
                });

        return stats;
    }

    @Override
    public AttendanceRecord getRecordByDate(String empId, Date recordDate) {
        QueryWrapper<AttendanceRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("emp_id", empId)
                .eq("record_date", recordDate);
        return this.getOne(queryWrapper);
    }
    @Override
    public List<AttendanceRecord> getAttendanceByEmpName(String empName, int year, int month) {
        return attendanceMapper.getRecordsByEmpName(empName, year, month);
    }

}