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

    <!-- Thư viện -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        .chart-box {
            width: 750px;
            max-width: 750px;
            height: 500px;
            display: flex;
            flex-direction: column;
        }
        .chart-box canvas {
            height: 400px !important;
            width: 100% !important;
        }
    </style>
</head>

<body class="bg-gray-100">
    <%@ include file="admin/navofadmin.jsp" %>

    <div class="container mx-auto p-6 max-w-7xl">
        <div class="flex justify-center items-start gap-5 mt-6">
            <div class="bg-white shadow-lg rounded-lg p-6 chart-box">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-lg font-semibold">Tỷ lệ mua gói</h2>
                    <select id="timePeriod" class="border p-2 rounded">
                        <option value="month" <c:if test="${periodDefault == 'month'}">selected</c:if>>Tháng</option>
                        <option value="year" <c:if test="${periodDefault == 'year'}">selected</c:if>>Năm</option>
                    </select>
                </div>

                <canvas id="revenueChart"></canvas>
                <p class="text-center mt-4">Số người mua: <span id="purchaserCount">0</span></p>
                <p class="text-center mt-2">Tổng doanh thu: <span id="totalRevenue">0</span> VND</p>
            </div>
        </div>
    </div>

    <script>
        Chart.register(ChartDataLabels);
        const revenueCtx = document.getElementById('revenueChart').getContext('2d');

        const revenueChart = new Chart(revenueCtx, {
            type: 'pie',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    customData: [],
                    backgroundColor: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444'],
                    borderColor: '#fff',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'bottom' },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const label = context.label || '';
                                const value = context.raw || 0; // Ensure value is used
                                const percent = context.dataset.customData[context.dataIndex] || '0';
                                return `${label}: ${value} giao dịch (${percent}%)`;
                            }
                        }
                    },
                    datalabels: {
                        color: '#fff',
                        anchor: 'center',
                        align: 'center',
                        formatter: function (value, context) { 
                            const percent = context.dataset.customData[context.dataIndex] || '0';
                            return percent + '%';
                        },
                        font: { weight: 'bold', size: 12 }
                    }
                }
            },
            plugins: [ChartDataLabels]
        });

        async function fetchRevenueData(period) {
            const validPeriod = period || 'month';
            const url = '<%= request.getContextPath() %>/api/revenuestat?period=' + validPeriod + '&t=' + Date.now();
            try {
                const response = await fetch(url, { cache: 'no-store' });
                if (!response.ok) throw new Error(`Lỗi API: ${response.status}`);
                const data = await response.json();

                const labels = data.plans.map(item => item.plan || 'Không xác định');
                const counts = data.plans.map(item => item.count || 0);
                const percents = data.plans.map(item => item.percent || '0');

                revenueChart.data.labels = labels;
                revenueChart.data.datasets[0].data = counts;
                revenueChart.data.datasets[0].customData = percents;
                revenueChart.update();

                document.getElementById('purchaserCount').textContent = (data.purchaserCount || 0).toLocaleString('vi-VN');
                document.getElementById('totalRevenue').textContent = (data.totalRevenue || 0).toLocaleString('vi-VN');
            } catch (error) {
                console.error('Lỗi khi lấy dữ liệu doanh thu:', error);
                alert('Không thể tải dữ liệu biểu đồ doanh thu.');
                revenueChart.data.labels = ['Lỗi'];
                revenueChart.data.datasets[0].data = [0];
                revenueChart.data.datasets[0].customData = ['0'];
                revenueChart.update();
                document.getElementById('purchaserCount').textContent = '0';
                document.getElementById('totalRevenue').textContent = '0';
            }
        }

        document.addEventListener('DOMContentLoaded', () => fetchRevenueData('month'));
        document.getElementById('timePeriod').addEventListener('change', function () {
            fetchRevenueData(this.value);
        });
    </script>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>