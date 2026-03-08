document.addEventListener('DOMContentLoaded', function() {
    // 初始化日期选择器
    initYearSelect('#year');
    initMonthSelect('#month');
    initYearSelect('#deptYear');
    initMonthSelect('#deptMonth');
    initYearSelect('#companyYear');
    initMonthSelect('#companyMonth');

    // 加载部门列表
    loadDepartments();

    // 员工考勤查询
    document.getElementById('employee-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        const empName = document.getElementById('empName').value;
        const year = document.getElementById('year').value;
        const month = document.getElementById('month').value;

        try {
            const response = await fetch(`/api/admin/attendance/employee?empName=${empName}&year=${year}&month=${month}`);
            if (!response.ok) {
                throw new Error('请求失败，状态码：' + response.status);
            }
            const records = await response.json();
            displayEmployeeResults(records);
        } catch (error) {
            console.error('获取考勤记录失败:', error);
            showNotification('查询失败: ' + error.message, 'error');
        }
    });

    // 部门考勤查询
    document.getElementById('dept-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        const deptName = document.getElementById('deptSelect').value;
        const year = document.getElementById('deptYear').value;
        const month = document.getElementById('deptMonth').value;

        try {
            const data = await fetchData(`/api/admin/attendance/stats?deptName=${encodeURIComponent(deptName)}&year=${year}&month=${month}`);
            displayDeptResults(data.records);
            renderStatsChart(data.stats);
        } catch (error) {
            showNotification('查询失败: ' + error.message, 'error');
        }
    });


    // 公司考勤查询
    document.getElementById('company-form').addEventListener('submit', async function(e) {
        e.preventDefault();
        const year = document.getElementById('companyYear').value;
        const month = document.getElementById('companyMonth').value;

        try {
            const data = await fetchData(`/api/admin/attendance/stats?year=${year}&month=${month}`);
            displayCompanyResults(data.records);
            renderStatsChart(data.stats);
        } catch (error) {
            showNotification('查询失败: ' + error.message, 'error');
        }
    });

    // 加载部门列表
    async function loadDepartments() {
        try {
            const depts = await fetchData('/api/admin/attendance/departments');
            const select = document.getElementById('deptSelect');
            select.innerHTML = '<option value="">请选择部门</option>';

            depts.forEach(dept => {
                const option = document.createElement('option');
                option.value = dept.deptName;
                option.textContent = dept.deptName;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('加载部门列表失败:', error);
            //showNotification('加载部门列表失败', 'error');
        }
    }
    function displayEmployeeResults(records) {
        const tbody = document.getElementById('employee-records');
        if (!tbody) {
            console.error('未找到 employee-records 元素');
            return;
        }
        tbody.innerHTML = '';

        if (records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">没有找到考勤记录</td></tr>';
            return;
        }

        try {
            records.forEach(record => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${formatDate(record.recordDate)}</td>
                    <td>${record.empName}</td>
                    <td>${record.deptName}</td>
                    <td>${record.signInTime || '未签到'}</td>
                    <td>${record.signOutTime || '未签退'}</td>
                    <td class="status-${record.status}">${record.status}</td>
                `;
                tbody.appendChild(row);
            });
        } catch (error) {
            console.error('显示考勤记录失败:', error);
        }
    }

    // 显示部门统计结果
    function displayDeptResults(records) {
        const tbody = document.getElementById('dept-stats');
        if (!tbody) {
            console.error('未找到 dept-stats 元素');
            return;
        }
        tbody.innerHTML = '';

        if (records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">没有找到统计数据</td></tr>';
            return;
        }

        records.forEach(record => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${stat.deptName}</td>
                <td>${stat.normalCount || 'N/A'}</td>
                <td>${stat.lateCount || 'N/A'}</td>
                <td>${stat.earlyCount || 'N/A'}</td>
                <td>${stat.absentCount || 'N/A'}</td>
                <td>${stat.insufficientHoursCount || 'N/A'}</td>
            `;
            tbody.appendChild(row);
        });
    }

    // 显示公司统计结果
    function displayCompanyResults(records) {
        const tbody = document.getElementById('company-stats');
        if (!tbody) {
            console.error('未找到 company-stats 元素');
            return;
        }
        tbody.innerHTML = '';

        if (records.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">没有找到统计数据</td></tr>';
            return;
        }

        records.forEach(record => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${record.deptName}</td>
                <td>${record.normalCount}</td>
                <td>${record.lateCount}</td>
                <td>${record.earlyCount}</td>
                <td>${record.absentCount}</td>
                <td>${record.insufficientHoursCount}</td>
            `;
            tbody.appendChild(row);
        });
    }

    // 渲染统计图表
    function renderStatsChart(stats) {
        const ctx = document.getElementById('companyStatsChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['正常', '迟到', '早退', '缺勤', '工时不足'],
                datasets: [{
                    label: '考勤统计',
                    data: [stats.normalCount, stats.lateCount, stats.earlyCount, stats.absentCount, stats.insufficientHoursCount],
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(255, 205, 86, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(153, 102, 255, 0.2)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)',
                        'rgba(255, 205, 86, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(153, 102, 255, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // 渲染统计图表
    let companyStatsChart;
    let deptComparisonChart;

    function renderStatsChart(stats) {
        // 销毁之前的图表实例
        if (companyStatsChart) {
            companyStatsChart.destroy();
        }
        if (deptComparisonChart) {
            deptComparisonChart.destroy();
        }

        // 重新绘制公司考勤统计图表
        const companyStatsCtx = document.getElementById('companyStatsChart').getContext('2d');
        companyStatsChart = new Chart(companyStatsCtx, {
            type: 'bar',
            data: {
                labels: ['正常', '迟到', '早退', '缺勤', '工时不足'],
                datasets: [{
                    label: '考勤统计',
                    data: [stats.normalCount, stats.lateCount, stats.earlyCount, stats.absentCount, stats.insufficientHoursCount],
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(255, 205, 86, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(153, 102, 255, 0.2)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)',
                        'rgba(255, 205, 86, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(153, 102, 255, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        // 重新绘制部门考勤对比图表
        const deptComparisonCtx = document.getElementById('deptComparisonChart').getContext('2d');
        deptComparisonChart = new Chart(deptComparisonCtx, {
            type: 'bar',
            data: {
                labels: ['部门1', '部门2', '部门3'], // 这里需要根据实际数据动态生成标签
                datasets: [{
                    label: '部门考勤对比',
                    data: [10, 20, 30], // 这里需要根据实际数据动态生成数据
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(255, 99, 132, 0.2)',
                        'rgba(255, 205, 86, 0.2)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)',
                        'rgba(255, 205, 86, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
    // 格式化日期
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
});