package com.test.sshProject.service;

import com.test.sshProject.entity.Dept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeptService extends IService<Dept> {

    // 获取所有部门列表
    List<Dept> listAllDepts();
    // 添加这个方法
    Dept getById(Integer id);
    Dept getByName(String deptName);

}