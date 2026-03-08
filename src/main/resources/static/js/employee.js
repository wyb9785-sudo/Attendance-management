/**
 * 员工页面JS - employee.js
 */

document.addEventListener('DOMContentLoaded', function() {
    // 初始化日期筛选器
    initYearSelect('#year-filter');
    initMonthSelect('#month-filter');
    loadLeaveRecords();
    // 绑定刷新按钮事件
    document.getElementById('refresh-btn').addEventListener('click', loadLeaveRecords);
    // 获取当前用户ID (实际应从安全上下文中获取)
    const currentUserId = 'current-user-id';

    // 签到功能
    document.getElementById('sign-in-btn').addEventListener('click', async function() {
        try {
            const response = await fetch('/api/employee/attendance/sign-in', {
                method: 'POST',
                credentials: 'include' // 确保携带cookie
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || '签到失败');
            }

            const result = await response.json();
            showNotification('签到成功', 'success');
            updateAttendanceStatus();
        } catch (error) {
            showNotification('签到失败: ' + error.message, 'error');
            console.error('签到错误:', error);
            // 如果未登录，跳转到登录页
            if (error.message.includes('未登录')) {
                window.location.href = '/login';
            }
        }
    });
    //签退
    document.getElementById('sign-out-btn').addEventListener('click', async function() {
        try {
            const response = await fetch('/api/employee/attendance/sign-out', {
                method: 'POST',
                credentials: 'include'
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || '签退失败');
            }

            const result = await response.json();
            showNotification(result.message, 'success');
            updateAttendanceStatus();
            loadAttendanceRecords();
        } catch (error) {
            showNotification('签退失败: ' + error.message, 'error');
            console.error('签退错误:', error);
            if (error.message.includes('未登录')) {
                window.location.href = '/login';
            }
        }
    });
    // 加载考勤记录
    async function loadAttendanceRecords() {
        const year = document.getElementById('year-filter').value;
        const month = document.getElementById('month-filter').value;

        try {
            const response = await fetch(`/api/employee/attendance/detail?year=${year}&month=${month}`, {
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('加载记录失败');
            }

            const records = await response.json();
            displayAttendanceRecords(records);
            renderMonthStats(records);
        } catch (error) {
            showNotification('加载记录失败: ' + error.message, 'error');
            console.error('加载记录错误:', error);
        }
    }

// 显示考勤记录
    function displayAttendanceRecords(records) {
        const tbody = document.getElementById('attendance-records');
        tbody.innerHTML = '';

        if (!records || records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">没有找到考勤记录</td></tr>';
            return;
        }

        records.forEach(record => {
            const row = document.createElement('tr');
            const date = new Date(record.recordDate);
            const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
            const weekday = weekdays[date.getDay()];

            row.innerHTML = `
            <td>${date.getMonth() + 1}月${date.getDate()}日</td>
            <td>星期${weekday}</td>
            <td>${record.signInTime || '未签到'}</td>
            <td>${record.signOutTime || '未签退'}</td>
            <td>${record.workHours ? record.workHours.toFixed(1) + '小时' : '-'}</td>
            <td class="status-${record.status}">${record.status}</td>
            <td>${record.remark || '-'}</td>
        `;
            tbody.appendChild(row);
        });
    }
    // 更新考勤状态显示
    async function updateAttendanceStatus() {
        try {
            const today = new Date().toISOString().split('T')[0];
            const response = await fetch(`/api/employee/attendance/today?date=${today}`, {
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('获取今日考勤失败');
            }

            const record = await response.json();
            const statusDiv = document.getElementById('attendance-status');
            const signInBtn = document.getElementById('sign-in-btn');
            const signOutBtn = document.getElementById('sign-out-btn');

            if (record.signInTime && record.signOutTime) {
                statusDiv.innerHTML = `
                <p class="text-success fw-bold">今日已签退</p>
                <p class="text-muted small">签到: ${record.signInTime}</p>
                <p class="text-muted small">签退: ${record.signOutTime}</p>
            `;
                signInBtn.disabled = true;
                signOutBtn.disabled = true;
            } else if (record.signInTime) {
                statusDiv.innerHTML = `
                <p class="text-warning fw-bold">已签到</p>
                <p class="text-muted small">时间: ${record.signInTime}</p>
            `;
                signInBtn.disabled = true;
                signOutBtn.disabled = false;
            } else {
                statusDiv.innerHTML = '<p class="text-danger fw-bold">今日未签到</p>';
                signInBtn.disabled = false;
                signOutBtn.disabled = true;
            }
        } catch (error) {
            console.error('更新状态失败:', error);
            // 如果未登录，跳转到登录页
            if (error.message.includes('未登录')) {
                window.location.href = '/login';
            }
        }
    }
    // 加载请假记录
    async function loadLeaveRecords() {
        const status = document.getElementById('status-filter').value;
        const response = await fetch(`/api/employee/leave/list?status=${status}`, {
            credentials: 'include'
        });
        const leaves = await response.json();
        displayLeaveRecords(leaves);

    }

    //显示考勤记录
    function displayAttendanceRecords(records) {
        const tbody = document.getElementById('attendance-records');
        tbody.innerHTML = '';

        if (!records || records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">本月无考勤记录</td></tr>';
            return;
        }

        records.forEach(record => {
            const row = document.createElement('tr');
            const date = new Date(record.recordDate);
            const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
            const weekday = weekdays[date.getDay()];

            // 处理未签到/未签退情况
            const signInTime = record.signInTime ? record.signInTime.substring(0, 5) : '未签到';
            const signOutTime = record.signOutTime ? record.signOutTime.substring(0, 5) : '未签退';
            const workHours = record.workHours ? record.workHours.toFixed(1) + '小时' : '-';
            const status = record.status || '未签到';

            row.innerHTML = `
            <td>${date.getMonth() + 1}月${date.getDate()}日</td>
            <td>星期${weekday}</td>
            <td>${signInTime}</td>
            <td>${signOutTime}</td>
            <td>${workHours}</td>
            <td class="status-${status}">${status}</td>
            <td>${record.remark || '-'}</td>
        `;
            tbody.appendChild(row);
        });

        // 绑定取消按钮事件
        document.querySelectorAll('.cancel-btn').forEach(btn => {
            btn.addEventListener('click', async function() {
                const leaveId = this.getAttribute('data-id');
                try {
                    const response = await fetch(`/employee/leave/cancel/${leaveId}`, {
                        method: 'DELETE',
                        credentials: 'include'
                    });

                    if (!response.ok) {
                        throw new Error('取消请假失败');
                    }

                    showNotification('请假已取消', 'success');
                    loadLeaveRecords();
                } catch (error) {
                    showNotification('取消请假失败: ' + error.message, 'error');
                }
            });
        });
    }

// 提交请假申请
    document.getElementById('leave-form').addEventListener('submit', async function(e) {
        e.preventDefault();

        const leaveData = {
            leaveType: document.getElementById('leave-type').value,
            startDate: document.getElementById('start-date').value,
            endDate: document.getElementById('end-date').value,
            reason: document.getElementById('reason').value
        };

        try {
            const response = await fetch('/employee/leave/apply', {  // 确保路径正确
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(leaveData),
                credentials: 'include'
            });

            const result = await response.json();  // 解析响应数据

            if (!response.ok) {
                throw new Error(result.error || '提交请假申请失败');
            }

            showNotification('请假申请提交成功', 'success');
            this.reset();
            loadLeaveRecords();  // 重新加载请假记录

        } catch (error) {
            showNotification('提交请假申请失败: ' + error.message, 'error');
            console.error('提交请假申请错误:', error);
        }
    });
    // 显示请假记录
    function displayLeaveRecords(leaves) {
        const tbody = document.getElementById('leave-records');
        tbody.innerHTML = '';

        if (!records || records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">没有找到请假记录</td></tr>';
            return;
        }

        leaves.forEach(leave => {
            const row = document.createElement('tr');
            const startDate = new Date(leave.startDate);
            const endDate = new Date(leave.endDate);
            const days = (endDate - startDate) / (1000 * 60 * 60 * 24);

            row.innerHTML = `
            <td>${leave.leaveType}</td>
            <td>${startDate.toISOString().split('T')[0]} - ${endDate.toISOString().split('T')[0]}</td>
            <td>${days}</td>
            <td>${leave.reason}</td>
            <td>${leave.status}</td>
            <td>${leave.approveOpinion || '-'}</td>
            <td>
                ${leave.status === '待审批' ? `<button class="btn btn-sm btn-danger cancel-btn" data-id="${leave.leaveId}">取消</button>` : ''}
            </td>
        `;
            tbody.appendChild(row);
        });

        // 绑定取消按钮事件
        document.querySelectorAll('.cancel-btn').forEach(btn => {
            btn.addEventListener('click', async function() {
                const leaveId = this.getAttribute('data-id');
                try {
                    const response = await fetch(`/api/employee/leave/cancel/${leaveId}`, {
                        method: 'DELETE',
                        credentials: 'include'
                    });

                    if (!response.ok) {
                        throw new Error('取消请假失败');
                    }

                    showNotification('请假已取消', 'success');
                    loadLeaveRecords();
                } catch (error) {
                    showNotification('取消请假失败: ' + error.message, 'error');
                }
            });
        });
    }
    // 更新考勤状态显示
    async function updateAttendanceStatus() {
        try {
            const today = new Date().toISOString().split('T')[0];
            const response = await fetch(`/api/employee/attendance/today?date=${today}`, {
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error('获取今日考勤失败');
            }

            const record = await response.json();
            const statusDiv = document.getElementById('attendance-status');
            const signInBtn = document.getElementById('sign-in-btn');
            const signOutBtn = document.getElementById('sign-out-btn');

            if (record.signInTime && record.signOutTime) {
                statusDiv.innerHTML = `
                <p class="text-success fw-bold">今日已签退</p>
                <p class="text-muted small">签到: ${record.signInTime}</p>
                <p class="text-muted small">签退: ${record.signOutTime}</p>
            `;
                signInBtn.disabled = true;
                signOutBtn.disabled = true;
            } else if (record.signInTime) {
                statusDiv.innerHTML = `
                <p class="text-warning fw-bold">已签到</p>
                <p class="text-muted small">时间: ${record.signInTime}</p>
            `;
                signInBtn.disabled = true;
                signOutBtn.disabled = false;
            } else {
                statusDiv.innerHTML = '<p class="text-danger fw-bold">今日未签到</p>';
                signInBtn.disabled = false;
                signOutBtn.disabled = true;
            }
        } catch (error) {
            console.error('更新状态失败:', error);
            // 如果未登录，跳转到登录页
            if (error.message.includes('未登录')) {
                window.location.href = '/login';
            }
        }
    }

    // 初始化页面
    updateAttendanceStatus();
    loadAttendanceRecords();

    // 筛选按钮事件
    document.getElementById('filter-btn').addEventListener('click', loadAttendanceRecords);
});