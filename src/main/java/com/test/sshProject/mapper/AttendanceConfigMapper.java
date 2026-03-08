package com.test.sshProject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.sshProject.entity.AttendanceConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttendanceConfigMapper extends BaseMapper<AttendanceConfig> {

    // 根据部门ID获取考勤配置
    AttendanceConfig getByDeptId(Integer deptId);
}
