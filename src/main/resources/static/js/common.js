/**
 * 公共工具函数 - common.js
 */

// 显示通知消息
function showNotification(message, type = 'success') {
    // 移除已有的通知
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="toast show">
            <div class="toast-header">
                <strong class="me-auto">${type === 'success' ? '成功' : '错误'}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">${message}</div>
        </div>
    `;

    document.body.appendChild(notification);

    // 自动隐藏
    setTimeout(() => {
        notification.querySelector('.toast').classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);

    // 点击关闭按钮
    notification.querySelector('.btn-close').addEventListener('click', () => {
        notification.querySelector('.toast').classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    });
}

// AJAX请求封装
async function fetchData(url, method = 'GET', data = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            // 可以在这里添加认证头
        },
        credentials: 'include' // 包含cookie
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || '请求失败');
    }

    return await response.json();
}

// 初始化年份选择器
function initYearSelect(selector, startYear = 2020, endYear = new Date().getFullYear()) {
    const select = document.querySelector(selector);
    if (!select) return;

    select.innerHTML = '';
    for (let year = endYear; year >= startYear; year--) {
        const option = document.createElement('option');
        option.value = year;
        option.textContent = year;
        select.appendChild(option);
    }
    select.value = new Date().getFullYear();
}

// 初始化月份选择器
function initMonthSelect(selector) {
    const select = document.querySelector(selector);
    if (!select) return;

    select.innerHTML = '';
    for (let month = 1; month <= 12; month++) {
        const option = document.createElement('option');
        option.value = month;
        option.textContent = `${month}月`;
        select.appendChild(option);
    }
    select.value = new Date().getMonth() + 1;
}

// 格式化日期
function formatDate(dateStr) {
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}月${date.getDate()}日`;
}

// 计算工作日天数
function countWorkingDays(startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    let count = 0;

    while (start <= end) {
        const day = start.getDay();
        if (day !== 0 && day !== 6) { // 0是周日，6是周六
            count++;
        }
        start.setDate(start.getDate() + 1);
    }

    return count;
}