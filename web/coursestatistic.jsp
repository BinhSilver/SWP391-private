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
                width: 750px; /* Fixed width for consistency */
                max-width: 750px; /* Prevent overflow */
                height: 500px; /* Fixed height */
                display: flex;
                flex-direction: column;
            }
            .chart-box canvas {
                height: 400px !important; /* Fixed canvas height */
                width: 100% !important; /* Ensure canvas fits container */
            }
        </style>
    </head>
    <body class="bg-gray-100">
        <%@ include file="admin/navofadmin.jsp" %>
        <div class="container mx-auto p-6 max-w-7xl">
            <!-- Container cho các biểu đồ -->
            <div class="flex justify-center items-start gap-5 mt-6">
                <!-- Bar Chart: Tạo khóa học -->
                <div class="bg-white shadow-lg rounded-lg p-6 chart-box">
                    <div class="flex justify-between items-center mb-6">
                        <h2 class="text-lg font-semibold">Tạo khóa học</h2>
                        <div>
                            <button id="courseMonthBtn" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">Tháng</button>
                            <button id="courseYearBtn" class="bg-blue-500 text-white px-4 py-2 rounded">Năm</button>
                        </div>
                    </div>
                    <canvas id="courseChart"></canvas>
                </div>

                <!-- Pie Chart: Tỉ lệ khóa học bị ẩn -->
                <div class="bg-white shadow-lg rounded-lg p-6 chart-box">
                    <h2 class="text-lg font-semibold mb-6 text-center">Tỉ lệ khóa học bị ẩn</h2>
                    <canvas id="hiddenCourseChart"></canvas>
                </div>
            </div>
        </div>

        <script>
            // Register Chart.js Data Labels plugin
            Chart.register(ChartDataLabels);

            const courseCtx = document.getElementById('courseChart').getContext('2d');
            const hiddenCourseCtx = document.getElementById('hiddenCourseChart').getContext('2d');

            const courseChart = new Chart(courseCtx, {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'Tạo khóa học',
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
                        x: { title: { display: true, text: 'Thời gian' } }
                    }
                }
            });

            const hiddenCourseChart = new Chart(hiddenCourseCtx, {
                type: 'pie',
                data: {
                    labels: ['Hiển thị', 'Bị ẩn'],
                    datasets: [{
                        label: 'Tỉ lệ khóa học',
                        data: [0, 0],
                        backgroundColor: ['rgba(75, 192, 192, 0.5)', 'rgba(255, 99, 132, 0.5)'],
                        borderColor: ['rgba(75, 192, 192, 1)', 'rgba(255, 99, 132, 1)'],
                        borderWidth: 1
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
                                    const value = context.raw || 0;
                                    const data = context.chart.data.datasets[0].data;
                                    const total = data.reduce((sum, val) => sum + (val >= 0 ? val : 0), 0);
                                    const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                    return `${label}: ${value} khóa học (${percent}%)`;
                                }
                            }
                        },
                        datalabels: {
                            color: '#fff',
                            formatter: function (value, context) {
                                const data = context.chart.data.datasets[0].data;
                                const total = data.reduce((sum, val) => sum + (val >= 0 ? val : 0), 0);
                                const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                return total > 0 ? `${value} (${percent}%)` : `0 (0%)`;
                            },
                            font: { weight: 'bold', size: 12 },
                            clip: false
                        }
                    }
                },
                plugins: [ChartDataLabels]
            });

            async function fetchChartData(period) {
                try {
                    const contextPath = '<%= request.getContextPath()%>';
                    let url = contextPath + '/CourseStatsServlet?type=chart&period=' + period;
                    const response = await fetch(url);
                    if (!response.ok) {
                        throw new Error('Lỗi API: ' + response.status + ' ' + response.statusText);
                    }
                    const data = await response.json();
                    console.log('Dữ liệu biểu đồ khóa học:', JSON.stringify(data));
                    courseChart.data.labels = data.map(item => item.period);
                    courseChart.data.datasets[0].data = data.map(item => item.count);
                    courseChart.update();
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu biểu đồ:', error.message);
                    alert('Không thể tải dữ liệu tạo khóa học. Vui lòng kiểm tra console.');
                }
            }

            async function fetchHiddenCourseData() {
                try {
                    const response = await fetch('<%= request.getContextPath()%>/CourseStatsServlet?type=hidden');
                    if (!response.ok) {
                        throw new Error('Lỗi HTTP: ' + response.status);
                    }
                    const data = await response.json();
                    console.log('Dữ liệu tỉ lệ ẩn:', data);
                    return data;
                } catch (error) {
                    console.error('Lỗi khi lấy dữ liệu tỉ lệ ẩn:', error);
                    return { hidden: 0, visible: 0 };
                }
            }

            async function updateHiddenCourseChart() {
                const data = await fetchHiddenCourseData();
                hiddenCourseChart.data.datasets[0].data = [data.visible, data.hidden];
                hiddenCourseChart.update();
            }

            document.addEventListener('DOMContentLoaded', () => {
                fetchChartData('month');
                updateHiddenCourseChart();
            });

            document.getElementById('courseMonthBtn').addEventListener('click', function () {
                fetchChartData('month');
            });

            document.getElementById('courseYearBtn').addEventListener('click', function () {
                fetchChartData('year');
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>