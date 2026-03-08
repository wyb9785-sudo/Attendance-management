package com.test.sshProject.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//部门
@Data
@TableName("dept")
public class Dept {
    private Integer deptId;
    private String deptName;
    private String location;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getLocation() {
        return location;
    }
}
