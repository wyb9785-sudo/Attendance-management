package com.test.sshProject.service;

import com.test.sshProject.entity.AttendanceConfig;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AttendanceConfigService extends IService<AttendanceConfig> {

    // 根据部门ID获取考勤配置
    AttendanceConfig getByDeptId(Integer deptId);
}
