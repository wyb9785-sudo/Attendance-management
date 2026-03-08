package com.test.sshProject.service.impl;

import com.test.sshProject.entity.AttendanceConfig;
import com.test.sshProject.mapper.AttendanceConfigMapper;
import com.test.sshProject.service.AttendanceConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AttendanceConfigServiceImpl extends ServiceImpl<AttendanceConfigMapper, AttendanceConfig> implements AttendanceConfigService {

    @Override
    public AttendanceConfig getByDeptId(Integer deptId) {
        return baseMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AttendanceConfig>()
                .eq("dept_id", deptId));
    }
}