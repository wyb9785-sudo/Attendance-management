package com.test.sshProject.service.impl;

import com.test.sshProject.entity.Dept;
import com.test.sshProject.mapper.DeptMapper;
import com.test.sshProject.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> listAllDepts() {
        return baseMapper.selectList(null);
    }

    @Override
    public Dept getByName(String deptName) {
        return lambdaQuery().eq(Dept::getDeptName, deptName).one();
    }
    @Override
    public Dept getById(Integer id) {
        return super.getById(id);
        // 调用父类的实现
    }
}