package com.test.sshProject.utils;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateUtils {

    // 周末列表
    private static final List<Integer> WEEKENDS = Arrays.asList(6, 7);
    // 周六、周日

    // 获取某个月的所有工作日
    public static List<LocalDate> getWorkDaysInMonth(int year, int month) {
        List<LocalDate> workDays = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWorkDay(date)) {
                workDays.add(date);
            }
        }

        return workDays;
    }

    // 判断是否是工作日
    public static boolean isWorkDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY
                && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    // LocalDate转java.sql.Date
    public static Date asDate(LocalDate localDate) {
        return Date.valueOf(localDate);
    }

    // java.util.Date转LocalDate
    public static LocalDate asLocalDate(java.util.Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // 计算两个日期之间的工作日天数
    public static long countWorkingDays(LocalDate startDate, LocalDate endDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays < 0) return 0;

        long workingDays = 0;
        for (long i = 0; i <= totalDays; i++) {
            LocalDate date = startDate.plusDays(i);
            if (isWorkDay(date)) {
                workingDays++;
            }
        }
        return workingDays;
    }
}