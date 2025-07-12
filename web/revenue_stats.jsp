<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
    User user = (User) session.getAttribute("authUser"); 
    if (user == null) { 
        response.sendRedirect("LoginJSP/LoginIndex.jsp"); 
        return; 
    } 
    request.setAttribute("periodDefault", "month"); 
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê doanh thu</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        .chart-box { background: white; border-radius: 15px; padding: 25px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); margin-bottom: 20px; transition: transform 0.3s ease; }
        .chart-box:hover { transform: translateY(-5px); }
        .chart-container { height: 350px; position: relative; }
        .stats-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 25px; border-radius: 15px; text-align: center; transition: transform 0.3s ease; }
        .stats-card:hover { transform: translateY(-5px); }
        .stats-number { font-size: 2rem; font-weight: bold; margin-bottom: 5px; }
        .stats-label { font-size: 0.9rem; opacity: 0.9; }
        .chart-title { font-size: 1.3rem; font-weight: 600; color: #1f2937; margin-bottom: 10px; text-align: center; }
        .chart-note { background: #f8fafc; border-left: 4px solid #3b82f6; padding: 10px 15px; margin: 10px 0; border-radius: 0 8px 8px 0; font-size: 0.85rem; color: #64748b; }
        .loading-spinner { display: inline-block; width: 20px; height: 20px; border: 3px solid #f3f3f3; border-top: 3px solid #3498db; border-radius: 50%; animation: spin 1s linear infinite; }
/*        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }*/
        .gradient-bg-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .gradient-bg-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        .gradient-bg-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        .gradient-bg-4 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
    </style>
</head>
<body class="bg-light">
    <%@ include file="admin/navofadmin.jsp" %>

    <div class="container-fluid px-4 py-4">
        <div class="text-center mb-4">
            <h1 class="h3 text-dark mb-2">📊 Dashboard Thống Kê Thanh Toán</h1>
            <p class="text-muted">Tổng quan doanh thu và giao dịch hệ thống</p>
        </div>

        <!-- Stats Cards -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3">
                <div class="stats-card gradient-bg-1">
                    <div class="stats-number" id="totalUsers"><div class="loading-spinner"></div></div>
                    <div class="stats-label">👤 Số người mua</div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="stats-card gradient-bg-2">
                    <div class="stats-number" id="totalRevenue"><div class="loading-spinner"></div></div>
                    <div class="stats-label">💰 Tổng doanh thu</div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="stats-card gradient-bg-3">
                    <div class="stats-number" id="totalTransactions"><div class="loading-spinner"></div></div>
                    <div class="stats-label">📈 Tổng giao dịch</div>
                </div>
            </div>
            <div class="col-md-3 mb-3">
                <div class="stats-card gradient-bg-4">
                    <div class="stats-number" id="uniqueUsers"><div class="loading-spinner"></div></div>
                    <div class="stats-label">🎯 Người dùng thanh toán</div>
                </div>
            </div>
        </div>

        <!-- Charts -->
        <div class="row">
            <div class="col-lg-4 mb-4">
                <div class="chart-box">
                    <div class="chart-title">🥧 Phân Tích Gói Premium</div>
                    <div class="chart-note"><i class="fas fa-info-circle"></i> Tỷ lệ % người dùng mua từng loại gói premium</div>
                    <select id="timePeriod" class="form-select mb-3" style="width: auto;">
                        <option value="month">📅 Tháng</option>
                        <option value="year">📅 Năm</option>
                    </select>
                    <div class="chart-container"><canvas id="revenueChart"></canvas></div>
                </div>
            </div>
            <div class="col-lg-4 mb-4">
                <div class="chart-box">
                    <div class="chart-title">📊 Trạng Thái Thanh Toán</div>
                    <div class="chart-note"><i class="fas fa-chart-bar"></i> Số lượng giao dịch theo từng trạng thái xử lý</div>
                    <div class="chart-container"><canvas id="statusChart"></canvas></div>
                </div>
            </div>
            <div class="col-lg-4 mb-4">
                <div class="chart-box">
                    <div class="chart-title">📈 Xu Hướng Theo Tháng</div>
                    <div class="chart-note"><i class="fas fa-chart-line"></i> Doanh thu (đường) và số giao dịch (cột) theo thời gian</div>
                    <div class="chart-container"><canvas id="trendsChart"></canvas></div>
                </div>
            </div>
        </div>
    </div>

    <script>
    Chart.register(ChartDataLabels);
    
    // Revenue Chart
    const revenueChart = new Chart(document.getElementById('revenueChart'), {
        type: 'doughnut',
        data: { labels: [], datasets: [{ data: [], customData: [], backgroundColor: ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'], borderColor: '#fff', borderWidth: 3 }] },
        options: {
            responsive: true, maintainAspectRatio: false, cutout: '60%',
            plugins: {
                legend: { position: 'bottom', labels: { padding: 15, usePointStyle: true, font: { size: 11 } } },
                tooltip: { backgroundColor: 'rgba(0,0,0,0.8)', titleColor: '#fff', bodyColor: '#fff' },
                datalabels: { color: '#fff', font: { weight: 'bold', size: 12 }, formatter: (value, context) => value > 0 ? context.dataset.customData[context.dataIndex] + '%' : '' }
            }
        }
    });

    // Status Chart  
    const statusChart = new Chart(document.getElementById('statusChart'), {
        type: 'bar',
        data: { labels: [], datasets: [{ data: [], backgroundColor: ['#10B981', '#EF4444', '#F59E0B'], borderColor: ['#059669', '#DC2626', '#D97706'], borderWidth: 2, borderRadius: 6 }] },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false }, datalabels: { anchor: 'end', align: 'top', color: '#374151', font: { weight: 'bold', size: 11 } } },
            scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
        }
    });

    // Trends Chart
    const trendsChart = new Chart(document.getElementById('trendsChart'), {
        type: 'bar',
        data: {
            labels: [],
            datasets: [
                { type: 'bar', label: 'Giao dịch', data: [], backgroundColor: 'rgba(59,130,246,0.7)', borderColor: '#3B82F6', borderWidth: 2, yAxisID: 'y' },
                { type: 'line', label: 'Doanh thu', data: [], borderColor: '#EF4444', backgroundColor: 'rgba(239,68,68,0.1)', borderWidth: 3, fill: true, tension: 0.4, yAxisID: 'y1' }
            ]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false }, datalabels: { display: false } },
            scales: {
                y: { type: 'linear', position: 'left', beginAtZero: true, title: { display: true, text: 'Giao dịch' } },
                y1: { type: 'linear', position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'Doanh thu' } }
            }
        }
    });

    // API Functions
    async function fetchRevenueData(period = 'month') {
        try {
            const response = await fetch('<%= request.getContextPath() %>/api/revenuestat?period=' + period + '&t=' + Date.now());
            if (!response.ok) throw new Error('API Error');
            const data = await response.json();
            
            revenueChart.data.labels = data.plans.map(item => item.plan || 'N/A');
            revenueChart.data.datasets[0].data = data.plans.map(item => item.count || 0);
            revenueChart.data.datasets[0].customData = data.plans.map(item => item.percent || '0');
            revenueChart.update();
            
            document.getElementById('totalUsers').textContent = (data.purchaserCount || 0).toLocaleString('vi-VN');
            document.getElementById('totalRevenue').textContent = (data.totalRevenue || 0).toLocaleString('vi-VN');
        } catch (error) {
            console.error('Revenue API Error:', error);
            document.getElementById('totalUsers').textContent = '0';
            document.getElementById('totalRevenue').textContent = '0';
        }
    }

    async function fetchStatusData() {
        try {
            const response = await fetch('<%= request.getContextPath() %>/api/paymentstatus?t=' + Date.now());
            if (!response.ok) throw new Error('API Error');
            const data = await response.json();
            
            statusChart.data.labels = data.statusStats.map(item => {
                switch(item.status) {
                    case 'Success': return 'Thành công';
                    case 'Failed': return 'Thất bại'; 
                    case 'Pending': return 'Đang chờ';
                    default: return item.status;
                }
            });
            statusChart.data.datasets[0].data = data.statusStats.map(item => item.count || 0);
            statusChart.update();
            
            document.getElementById('totalTransactions').textContent = (data.totalTransactionCount || 0).toLocaleString('vi-VN');
            document.getElementById('uniqueUsers').textContent = (data.uniqueUserCount || 0).toLocaleString('vi-VN');
        } catch (error) {
            console.error('Status API Error:', error);
            document.getElementById('totalTransactions').textContent = '0';
            document.getElementById('uniqueUsers').textContent = '0';
        }
    }

    async function fetchTrendsData() {
        try {
            const response = await fetch('<%= request.getContextPath() %>/api/paymenttrends?t=' + Date.now());
            if (!response.ok) throw new Error('API Error');
            const data = await response.json();
            
            trendsChart.data.labels = data.months || [];
            trendsChart.data.datasets[0].data = data.transactions || [];
            trendsChart.data.datasets[1].data = data.revenue || [];
            trendsChart.update();
        } catch (error) {
            console.error('Trends API Error:', error);
        }
    }

    async function loadAllData(period) {
        await Promise.all([fetchRevenueData(period), fetchStatusData(), fetchTrendsData()]);
    }

    // Event Listeners
    document.addEventListener('DOMContentLoaded', () => loadAllData('month'));
    document.getElementById('timePeriod').addEventListener('change', function() { loadAllData(this.value); });
    </script>
</body>
</html>