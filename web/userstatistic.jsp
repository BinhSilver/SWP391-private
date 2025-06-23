<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("LoginJSP/LoginIndex.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bảng điều khiển thống kê người dùng</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="css/usermanagecss.css">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
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
                        <h2 class="text-lg font-semibold">Đăng ký theo thời kỳ</h2>
                        <select id="timePeriod" class="border p-2 rounded">
                            <option value="month" selected>Tháng</option>
                            <option value="year">Năm</option>
                        </select>
                    </div>
                    <canvas id="registrationChart"></canvas>
                </div>
                <div class="bg-white shadow-lg rounded-lg p-6 chart-box">
                    <h2 class="text-lg font-semibold mb-6 text-center">Phân bố vai trò người dùng</h2>
                    <canvas id="userRoleChart"></canvas>
                </div>
            </div>
        </div>

        <script>
            Chart.register(ChartDataLabels);

            const registrationCtx = document.getElementById('registrationChart').getContext('2d');
            const registrationChart = new Chart(registrationCtx, {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'Người dùng',
                        data: [],
                        backgroundColor: 'rgba(153, 102, 255, 0.5)',
                        borderColor: 'rgba(153, 102, 255, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: { beginAtZero: true, title: { display: true, text: 'Số lượng đăng ký' } },
                        x: { title: { display: true, text: 'Thời kỳ' } }
                    }
                }
            });

            async function fetchChartData(period) {
                const validPeriod = period || 'month';
                const url = '<%= request.getContextPath()%>/api/userstats?period=' + validPeriod + '&t=' + Date.now();
                try {
                    console.log('Đang lấy dữ liệu thống kê người dùng cho thời kỳ:', validPeriod);
                    const response = await fetch(url, { cache: 'no-store' });
                    if (!response.ok) {
                        throw new Error(`Lỗi API: ${response.status} ${response.statusText}`);
                    }
                    const data = await response.json();
                    console.log('Dữ liệu thống kê người dùng thô:', data);

                    if (!Array.isArray(data)) {
                        throw new Error('Dữ liệu thống kê người dùng không phải là mảng');
                    }

                    const labels = data.map(item => item.period || 'Không xác định');
                    const counts = data.map(item => item.count >= 0 ? item.count : 0);
                    console.log('Nhãn biểu đồ đăng ký:', labels);
                    console.log('Số liệu biểu đồ đăng ký:', counts);

                    registrationChart.data.labels = labels;
                    registrationChart.data.datasets[0].data = counts;
                    registrationChart.update();
                    console.log('Biểu đồ đăng ký đã được cập nhật');
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu thống kê người dùng:', error.message);
                    alert('Không thể tải dữ liệu biểu đồ đăng ký. Vui lòng kiểm tra console.');
                    registrationChart.data.labels = ['Lỗi'];
                    registrationChart.data.datasets[0].data = [0];
                    registrationChart.update();
                }
            }

            const roleCtx = document.getElementById('userRoleChart').getContext('2d');
            const userRoleChart = new Chart(roleCtx, {
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
                                    console.log('Tooltip context:', {
                                        label: context.label,
                                        raw: context.raw,
                                        dataIndex: context.dataIndex,
                                        customData: context.dataset.customData
                                    });
                                    let label = context.label || '';
                                    let value = context.raw || 0;
                                    let percent = context.dataset.customData[context.dataIndex] || '0';
                                    return label + ': ' + value + ' người dùng (' + percent + '%)';
                                }
                            }
                        },
                        datalabels: {
                            color: '#fff',
                            anchor: 'center',
                            align: 'center',
                            formatter: function (value, context) {
                                console.log('Formatter context:', {
                                    value: value,
                                    data: context.chart.data.datasets[0].data,
                                    dataIndex: context.dataIndex,
                                    customData: context.dataset.customData
                                });
                                if (!context || !context.chart || !context.chart.data || !context.chart.data.datasets[0]) {
                                    console.error('Lỗi: context hoặc dữ liệu biểu đồ không hợp lệ');
                                    return '0%';
                                }
                                let percent = context.dataset.customData[context.dataIndex] || '0';
                                console.log('Nhãn biểu đồ vai trò - Phần trăm:', percent);
                                return percent + '%'; // Hiển thị phần trăm
                            },
                            font: { weight: 'bold', size: 12 },
                            clip: false
                        }
                    }
                },
                plugins: [ChartDataLabels]
            });

            async function loadChartData() {
                const url = '<%= request.getContextPath()%>/api/userrolestats?t=' + Date.now();
                try {
                    console.log('Đang lấy dữ liệu thống kê vai trò người dùng từ URL:', url);
                    const response = await fetch(url, { cache: 'no-store' });
                    console.log('Phản hồi API - Trạng thái:', response.status, 'OK:', response.ok);

                    if (!response.ok) {
                        throw new Error(`Lỗi API: ${response.status} ${response.statusText}`);
                    }

                    const data = await response.json();
                    console.log('Dữ liệu thống kê vai trò người dùng thô:', JSON.stringify(data, null, 2));

                    if (!Array.isArray(data)) {
                        throw new Error('Dữ liệu thống kê vai trò người dùng không phải là mảng');
                    }

                    console.log('Số bản ghi dữ liệu thô:', data.length);
                    console.log('Kiểm tra từng bản ghi dữ liệu thô:');
                    for (let i = 0; i < data.length; i++) {
                        let item = data[i];
                        console.log('Bản ghi ' + i + ': role=' + (item.role || 'undefined') + ', count=' + (item.count || 'undefined') + ', percent=' + (item.percent || 'undefined') + ', typeof count=' + typeof item.count);
                    }

                    const validData = data.filter(item => item.count >= 0 && typeof item.count === 'number');
                    console.log('Dữ liệu hợp lệ sau lọc:', JSON.stringify(validData, null, 2));
                    console.log('Số bản ghi hợp lệ:', validData.length);

                    const labels = validData.map(item => item.role || 'Không xác định');
                    const counts = validData.map(item => item.count || 0);
                    const percents = validData.map(item => item.percent || '0');
                    console.log('Nhãn biểu đồ vai trò:', labels);
                    console.log('Số liệu biểu đồ vai trò:', counts);
                    console.log('Phần trăm biểu đồ vai trò:', percents);

                    userRoleChart.data.labels = labels;
                    userRoleChart.data.datasets[0].data = counts;
                    userRoleChart.data.datasets[0].customData = percents;
                    console.log('Dữ liệu biểu đồ trước khi cập nhật:', JSON.stringify(userRoleChart.data, null, 2));
                    userRoleChart.update();
                    console.log('Biểu đồ vai trò đã được cập nhật');
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu thống kê vai trò:', error.message, error.stack);
                    alert('Không thể tải dữ liệu biểu đồ vai trò. Vui lòng kiểm tra console.');
                    userRoleChart.data.labels = ['Lỗi'];
                    userRoleChart.data.datasets[0].data = [0];
                    userRoleChart.data.datasets[0].customData = ['0'];
                    userRoleChart.update();
                }
            }

            document.addEventListener('DOMContentLoaded', async () => {
                await fetchChartData('month');
                await loadChartData();
            });

            document.getElementById('timePeriod').addEventListener('change', function () {
                fetchChartData(this.value);
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>