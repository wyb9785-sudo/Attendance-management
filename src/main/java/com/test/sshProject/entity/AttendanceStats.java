package com.test.sshProject.entity;
//考勤统计
// 该类为统计结果类，无需映射数据库表，因此无需 @TableName，当前代码正确。
import lombok.Data;

@Data
public class AttendanceStats {
    private int normalCount;
    // 正常次数
    private int lateCount;
    // 迟到次数
    private int earlyCount;
    // 早退次数
    private int absentCount;
    // 旷工次数
    private int insufficientHoursCount;
    // 工时不足次数
    public int getEarlyCount() {
        return earlyCount;
    }

    public void setNormalCount(int normalCount) {
        this.normalCount = normalCount;
    }

    public void setLateCount(int lateCount) {
        this.lateCount = lateCount;
    }

    public void setEarlyCount(int earlyCount) {
        this.earlyCount = earlyCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public void setInsufficientHoursCount(int insufficientHoursCount) {
        this.insufficientHoursCount = insufficientHoursCount;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public int getLateCount() {
        return lateCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public int getInsufficientHoursCount() {
        return insufficientHoursCount;
    }


}
