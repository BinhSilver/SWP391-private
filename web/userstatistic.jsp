
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
        <h1 class="text-2xl font-bold mb-4">User Registrations Chart</h1>

        <!-- Chart Container -->
        <div class="bg-white p-4 rounded-lg shadow">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-lg font-semibold">Registrations by Period</h2>
                <select id="timePeriod" class="border p-2 rounded">
                    <option value="month">Monthly</option>
                    <option value="week">Weekly</option>
                    <option value="day">Daily</option>
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
                    label: 'User Registrations',
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
                        title: { display: true, text: 'Number of Registrations' }
                    },
                    x: {
                        title: { display: true, text: 'Time Period' }
                    }
                }
            }
        });

        // Fetch and update chart data
        async function fetchChartData(period) {
            try {
                const contextPath = '<%= request.getContextPath() %>';
                const response = await fetch(`api/registrations?period=${period}`);
                if (!response.ok) {
                    throw new Error(`API error: ${response.status} ${response.statusText}`);
                }
                const data = await response.json();
                console.log('Registration Data:', data);

                registrationChart.data.labels = data.map(item => item.period);
                registrationChart.data.datasets[0].data = data.map(item => item.count);
                registrationChart.update();
            } catch (error) {
                console.error('Error fetching registration data:', error.message);
                alert('Không thể tải dữ liệu biểu đồ. Vui lòng kiểm tra console để biết chi tiết.');
            }
        }

        // Initial data fetch
        window.onload = function() {
            fetchChartData('month');
        };

        // Update chart on period change
        document.getElementById('timePeriod').addEventListener('change', function() {
            fetchChartData(this.value);
        });
    </script>
</body>
</html>
