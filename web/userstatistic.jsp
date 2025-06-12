<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="model.User" %>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("LoginJSP/LoginIndex.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Registrations Chart</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto p-6">
        <h1 class="text-2xl font-bold mb-6">Biểu đồ đăng ký người dùng</h1>

        <!-- Chart Container -->
        <div class="bg-white shadow-lg rounded-lg p-6">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-lg font-semibold">Đăng ký theo kỳ</h2>
                
               <select id="timePeriod" class="border p-2 rounded">
    <option value="month" selected>Tháng</option>
    <option value="year">Năm</option>              
</select>

            </div>
            <canvas id="registrationChart" height="200"></canvas>
        </div>
    </div>
    <script>
        // Initialize chart
        const registrationCtx = document.getElementById('registrationChart').getContext('2d');
        const registrationChart = new Chart(registrationCtx, {
            type: 'bar',
            data: {
                labels: [],
                datasets: [{
                    label: 'Người dùng ',
                    data: [],
                    backgroundColor: 'rgba(153, 102, 255, 0.5)',
                    borderColor: 'rgba(153, 102, 255, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true,
                        title: { display: true, text: 'Số lượng đăng ký' }
                    },
                    x: {
                        title: { display: true, text: 'Khoảng thời gian' }
                    }
                }
            }
        });

        // Fetch and update chart data
        async function fetchChartData(period) {
            const validPeriod = period || 'month'; // Dự phòng nếu period rỗng
            console.log('Gọi fetchChartData với period:', validPeriod);
            const url = '/SWP_HUY/api/userstats?period=' + validPeriod; // Dùng nối chuỗi
            console.log('URL yêu cầu:', url);
            try {
                const response = await fetch(url, {
                    cache: 'no-store' // Prevent caching
                });
                console.log('Trạng thái phản hồi:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error(`API error: ${response.status} ${response.statusText}`);
                }
                const data = await response.json();
                console.log('Registration Data:', data);

                // Ensure data is in correct format
                const labels = data.map(item => item.period || 'Unknown');
                const counts = data.map(item => item.count || 0);

                registrationChart.data.labels = labels;
                registrationChart.data.datasets[0].data = counts;
                registrationChart.update();
            } catch (error) {
                console.error('Error fetching registration data:', error.message);
                alert('Không thể tải dữ liệu biểu đồ. Vui lòng kiểm tra console để biết chi tiết.');
            }
        }

        // Initial data fetch
        document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM loaded, gọi fetchChartData với month');
            fetchChartData('month'); // Default to month
        });

        // Update chart on period change
        document.getElementById('timePeriod').addEventListener('change', function() {
            const selectedPeriod = this.value;
            console.log('Khoảng thời gian được chọn:', selectedPeriod);
            fetchChartData(selectedPeriod);
        });
    </script>
</body>
</html>