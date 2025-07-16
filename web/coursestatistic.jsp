<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <title>Thống kê Wasabii</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
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
                        <h2 class="text-lg font-semibold">Tạo khóa học</h2>
                        <select id="timePeriod" class="border p-2 rounded">
                            <option value="month" selected>Tháng</option>
                            <option value="year">Năm</option>
                        </select>
                    </div>
                    <canvas id="courseChart"></canvas>
                </div>
                <div class="bg-white shadow-lg rounded-lg p-6 chart-box">
                    <h2 class="text-lg font-semibold mb-6 text-center">Tỉ lệ khóa học bị ẩn</h2>
                    <canvas id="hiddenCourseChart"></canvas>
                </div>
            </div>
        </div>

        <script>
            Chart.register(ChartDataLabels);

            const courseCtx = document.getElementById('courseChart').getContext('2d');
            const courseChart = new Chart(courseCtx, {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'Khóa học',
                        data: [],
                        backgroundColor: 'rgba(75, 192, 192, 0.5)',
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: { beginAtZero: true, title: { display: true, text: 'Số lượng khóa học' } },
                        x: { title: { display: true, text: 'Thời kỳ' } }
                    }
                }
            });

            const hiddenCourseCtx = document.getElementById('hiddenCourseChart').getContext('2d');
            const hiddenCourseChart = new Chart(hiddenCourseCtx, {
                type: 'pie',
                data: {
                    labels: ['Hiển thị', 'Bị ẩn'],
                    datasets: [{
                        data: [],
                        customData: [],
                        backgroundColor: ['#3B82F6', '#EF4444'],
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
                                    return label + ': ' + value + ' khóa học (' + percent + '%)';
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
                                console.log('Nhãn biểu đồ tỉ lệ ẩn - Phần trăm:', percent);
                                return percent + '%';
                            },
                            font: { weight: 'bold', size: 12 },
                            clip: false
                        }
                    }
                },
                plugins: [ChartDataLabels]
            });

            async function fetchChartData(period) {
                const validPeriod = period || 'month';
                const url = '<%= request.getContextPath()%>/CourseStatsServlet?type=chart&period=' + validPeriod + '&t=' + Date.now();
                try {
                    console.log('Đang lấy dữ liệu thống kê khóa học cho thời kỳ:', validPeriod);
                    const response = await fetch(url, { cache: 'no-store' });
                    if (!response.ok) {
                        throw new Error(`Lỗi API: ${response.status} ${response.statusText}`);
                    }
                    const data = await response.json();
                    console.log('Dữ liệu thống kê khóa học thô:', JSON.stringify(data, null, 2));

                    if (!Array.isArray(data)) {
                        throw new Error('Dữ liệu thống kê khóa học không phải là mảng');
                    }

                    const labels = data.map(item => item.period || 'Không xác định');
                    const counts = data.map(item => item.count >= 0 ? item.count : 0);
                    console.log('Nhãn biểu đồ khóa học:', labels);
                    console.log('Số liệu biểu đồ khóa học:', counts);

                    courseChart.data.labels = labels;
                    courseChart.data.datasets[0].data = counts;
                    courseChart.update();
                    console.log('Biểu đồ khóa học đã được cập nhật');
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu thống kê khóa học:', error.message);
                    alert('Không thể tải dữ liệu biểu đồ khóa học. Vui lòng kiểm tra console.');
                    courseChart.data.labels = ['Lỗi'];
                    courseChart.data.datasets[0].data = [0];
                    courseChart.update();
                }
            }

            async function loadHiddenCourseData() {
                const url = '<%= request.getContextPath()%>/CourseStatsServlet?type=hidden&t=' + Date.now();
                try {
                    console.log('Đang lấy dữ liệu thống kê tỉ lệ khóa học ẩn từ URL:', url);
                    const response = await fetch(url, { cache: 'no-store' });
                    console.log('Phản hồi API - Trạng thái:', response.status, 'OK:', response.ok);

                    if (!response.ok) {
                        throw new Error(`Lỗi API: ${response.status} ${response.statusText}`);
                    }

                    const data = await response.json();
                    console.log('Dữ liệu thống kê tỉ lệ ẩn thô:', JSON.stringify(data, null, 2));

                    const validData = {
                        visible: data.visible >= 0 ? data.visible : 0,
                        hidden: data.hidden >= 0 ? data.hidden : 0
                    };
                    const total = validData.visible + validData.hidden;
                    const visiblePercent = total > 0 ? ((validData.visible / total) * 100).toFixed(1) : '0';
                    const hiddenPercent = total > 0 ? ((validData.hidden / total) * 100).toFixed(1) : '0';

                    console.log('Dữ liệu hợp lệ:', JSON.stringify(validData, null, 2));
                    console.log('Phần trăm hiển thị:', visiblePercent, 'Phần trăm ẩn:', hiddenPercent);

                    hiddenCourseChart.data.datasets[0].data = [validData.visible, validData.hidden];
                    hiddenCourseChart.data.datasets[0].customData = [visiblePercent, hiddenPercent];
                    console.log('Dữ liệu biểu đồ trước khi cập nhật:', JSON.stringify(hiddenCourseChart.data, null, 2));
                    hiddenCourseChart.update();
                    console.log('Biểu đồ tỉ lệ ẩn đã được cập nhật');
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu thống kê tỉ lệ ẩn:', error.message, error.stack);
                    alert('Không thể tải dữ liệu biểu đồ tỉ lệ ẩn. Vui lòng kiểm tra console.');
                    hiddenCourseChart.data.datasets[0].data = [0, 0];
                    hiddenCourseChart.data.datasets[0].customData = ['0', '0'];
                    hiddenCourseChart.update();
                }
            }

            document.addEventListener('DOMContentLoaded', async () => {
                await fetchChartData('month');
                await loadHiddenCourseData();
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