# 企联考勤云管理平台

### 📌 项目简介

企联考勤云管理平台是一套为企业提供完整员工考勤管理解决方案的现代化信息系统。平台涵盖员工考勤记录、请假管理、用户管理等核心功能，通过信息化手段优化企业考勤流程，提升管理效率，减少人为错误，并为管理层提供数据支持，助力企业实现数字化人力资源管理。

### 🎯 核心价值

* 高效便捷：员工可随时签到、请假，管理员可一键统计考勤数据

* 精准可靠：自动计算迟到、早退、缺勤，确保数据准确性

* 安全稳定：基于RBAC权限模型，保障数据安全与系统稳定性

*  灵活扩展：模块化设计，支持后续功能扩展

### 🛠️ 功能特性

#### 👤 员工端

* 个人考勤：签到/签退、查看今日考勤状态
* 考勤查询：按月统计个人考勤记录（正常、迟到、早退、缺勤）
* 请假管理：提交请假申请（事假/病假/公假）、取消待审批申请、查看请假记录

#### 👑 管理员端

    ✨ 考勤管理

* 员工考勤查询：按姓名、年月查询员工考勤明细
* 部门考勤统计：按部门、年月统计考勤数据
* 今日考勤看板：实时监控全员考勤状态


    ✨ 请假审批

* 待审批申请列表（支持按类型筛选）
* 审批操作（通过/驳回）及意见填写
* 全部记录查询（支持多维度筛选）


    ✨ 用户管理

* 用户增删改查
* 角色分配（管理员/普通员工）
* 状态管理（启用/禁用）

### 🏗️ 系统架构

| 层级 | 技术 |  说明 |
|:--:|:---|:---|
| 前端 | HTML5 + CSS3 + JavaScript<br/>Bootstrap + Thymeleaf<br/>ECharts | 页面结构<br/>响应式界面、模板引擎<br/>数据可视化 |
| 后端 | Spring Boot 2.3.7<br/>MyBatis-Plus 3.5.5<br/>PageHelper<br/>Swagger 3.0.0 | 基础框架<br/>ORM框架<br/>分页插件<br/>API文档 |
| 数据库 | MySQL 8.0.23<br/>Druid 1.2.24 | 关系型数据库<br/>数据库连接池 |

### 📊 数据库设计

| 表名 | 说明 |  关键字段 |
|:--:|:---|:---|
| employee | 员工信息表 |  emp_id, emp_name, dept_id, role, status |
| dept | 部门表 |  dept_id, dept_name, location |
| attendance_record | 考勤记录表 |  record_id, emp_id, sign_in_time, sign_out_time, status |
| leave_application | 请假申请表 |  leave_id, emp_id, leave_type, start_date, end_date, status |
| attendance_config | 考勤配置表 |  config_id, dept_id, work_start_time, work_end_time |
		


### 🚀 快速开始
#### 环境要求

* JDK 11+
* Maven 3.6+
* MySQL 8.0+
* Node.js (可选，用于前端资源构建)

#### 安装步骤
1.克隆项目

git clone https://github.com/your-repo/qilian-attendance.git

cd qilian-attendance

2.创建数据库

CREATE DATABASE attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

3.修改配置文件

src/main/resources/application.yml：

spring:

datasource:

url: jdbc:mysql://localhost:3306/attendance_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai

username: root

password: your_password

4.编译运行

mvn clean install

mvn spring-boot:run

5.访问系统

* 登录页: http://localhost:8080/login
* Swagger文档: http://localhost:8080/swagger-ui/

6.默认账号
角色	账号	密码	说明
管理员	admin	123456	拥有所有管理权限
普通员工	emp001	123456	普通员工权限


### 🔧 核心功能实现
#### 权限控制

基于自定义拦截器 RoleCheckInterceptor 实现RBAC权限管理：

* ROLE_MANAGER：访问 /admin/**
* ROLE_EMPLOYEE：访问 /employee/**
* 未登录用户：仅可访问登录页

#### 考勤规则引擎
* 迟到判定：签到时间 > 上班开始时间 + 迟到阈值
* 早退判定：签退时间 < 上班结束时间 - 早退阈值
* 工作时长计算：自动计算并判断是否满足工时要求


### 📈 运行效果
登录界面
https://media/image14.png

管理员仪表盘
https://media/image15.png

员工考勤
https://media/image16.png

### 🧪 测试

// 运行单元测试

mvn test

// 运行集成测试

mvn verify

### 📚 API文档

启动项目后访问：http://localhost:8080/swagger-ui/

主要接口：

* POST /api/login - 用户登录
* GET /api/attendance/today - 获取今日考勤
* POST /api/attendance/sign-in - 签到
* POST /api/attendance/sign-out - 签退
* POST /employee/leave/apply - 请假申请
* POST /admin/leave/approve - 审批请假

### 🔮 未来规划

* 功能扩展：加班管理、排班系统、移动端APP
* 技术升级：集成人脸识别签到、WebSocket实时推送
* 性能优化：Redis缓存、消息队列处理高并发
* 系统集成：对接ERP/HR系统



