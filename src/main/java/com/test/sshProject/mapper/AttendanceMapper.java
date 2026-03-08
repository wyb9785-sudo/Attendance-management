package com.test.sshProject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.sshProject.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AttendanceMapper extends BaseMapper<AttendanceRecord> {

    int updateById(@Param("record") AttendanceRecord record);
    // 根据日期查询考勤记录
    AttendanceRecord getRecordByDate(@Param("empId") String empId, @Param("recordDate") java.sql.Date recordDate);

    // 根据员工姓名模糊查询考勤记录
    @Select("SELECT ar.* FROM attendance_record ar " +
            "JOIN employee e ON ar.emp_id = e.emp_id " +
            "WHERE e.emp_name LIKE CONCAT('%', #{empName}, '%') " +
            "AND YEAR(ar.record_date) = #{year} " +
            "AND MONTH(ar.record_date) = #{month} " +
            "ORDER BY ar.record_date")
    List<AttendanceRecord> getRecordsByEmpName(
            @Param("empName") String empName,
            @Param("year") int year,
            @Param("month") int month
    );

    // 根据员工和年月查询考勤记录
    @Select("SELECT * FROM attendance_record WHERE emp_id = #{empId} " +
            "AND YEAR(record_date) = #{year} AND MONTH(record_date) = #{month} " +
            "ORDER BY record_date")
    List<AttendanceRecord> getRecordsByEmployee(
            @Param("empId") String empId,
            @Param("year") int year,
            @Param("month") int month
    );
    // 根据部门查询考勤记录
    List<AttendanceRecord> getRecordsByDept(@Param("deptId") int deptId,
                                            @Param("year") int year,
                                            @Param("month") int month);
    // 查询公司所有考勤记录
    List<AttendanceRecord> getRecordsByCompany(@Param("year") int year,
                                               @Param("month") int month);
}