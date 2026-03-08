package com.test.sshProject.dto;

import lombok.Data;

@Data
public class AttendanceStatsDTO {
    private int normalCount;
    private int lateCount;
    private int earlyCount;
    private int absentCount;
    private int insufficientHoursCount;
}