package com.test.sshProject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.test.sshProject.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
    Dept selectById(Integer id);
}