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
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>User Statistics Dashboard</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0/dist/chartjs-plugin-datalabels.min.js"></script>
        <script src="https://cdn.tailwindcss.com"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
          <link rel="stylesheet" href="css/usermanagecss.css">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    </head>
    <body class="bg-gray-100">
        <div class="container mx-auto p-6">
          
           <%@ include file="admin/navofadmin.jsp" %>
       

            <!-- Bar Chart: User Registrations -->
            <div class="bg-white shadow-lg rounded-lg p-6 mb-6">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-lg font-semibold">Registrations by Period</h2>
                    <select id="timePeriod" class="border p-2 rounded">
                        <option value="month" selected>Month</option>
                        <option value="year">Year</option>
                    </select>
                </div>
                <canvas id="registrationChart" height="200"></canvas>
            </div>

            <!-- Pie Chart: User Role Distribution -->
            <div class="bg-white shadow-lg rounded-lg p-6">
                <h2 class="text-lg font-semibold mb-6 text-center">User Role Distribution</h2>
                <canvas id="userRoleChart" style="min-height: 300px;"></canvas>
            </div>
        </div>

        <script>
            // Register Chart.js Data Labels plugin
            Chart.register(ChartDataLabels);

            // Bar Chart: User Registrations
            const registrationCtx = document.getElementById('registrationChart').getContext('2d');
            const registrationChart = new Chart(registrationCtx, {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'Users',
                        data: [],
                        backgroundColor: 'rgba(153, 102, 255, 0.5)',
                        borderColor: 'rgba(153, 102, 255, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: { beginAtZero: true, title: { display: true, text: 'Number of Registrations' } },
                        x: { title: { display: true, text: 'Time Period' } }
                    }
                }
            });

            async function fetchChartData(period) {
                const validPeriod = period || 'month';
                const url = '<%= request.getContextPath()%>/api/userstats?period=' + validPeriod + '&t=' + Date.now();
                try {
                    console.log('Fetching user stats for period:', validPeriod);
                    const response = await fetch(url, { cache: 'no-store' });
                    if (!response.ok) {
                        throw new Error(`API error: ${response.status} ${response.statusText}`);
                    }
                    const data = await response.json();
                    console.log('Raw user stats data:', data);

                    if (!Array.isArray(data)) {
                        throw new Error('User stats data is not an array');
                    }

                    const labels = data.map(item => item.period || 'Unknown');
                    const counts = data.map(item => item.count >= 0 ? item.count : 0);
                    console.log('Registration chart labels:', labels);
                    console.log('Registration chart counts:', counts);

                    registrationChart.data.labels = labels;
                    registrationChart.data.datasets[0].data = counts;
                    registrationChart.update();
                    console.log('Registration chart updated');
                } catch (error) {
                    console.error('Error fetching user stats:', error.message);
                    alert('Failed to load registration chart data. Check console for details.');
                    registrationChart.data.labels = ['Error'];
                    registrationChart.data.datasets[0].data = [0];
                    registrationChart.update();
                }
            }

            // Pie Chart: User Role Distribution
            const roleCtx = document.getElementById('userRoleChart').getContext('2d');
            const userRoleChart = new Chart(roleCtx, {
                type: 'pie',
                data: {
                    labels: [],
                    datasets: [{
                        data: [],
                        backgroundColor: ['#3B82F6', '#10B981', '#F59E0B', '#EF4444'],
                        borderColor: '#fff',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
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
                                    return `${label}: ${value} users (${percent}%)`;
                                }
                            }
                        },
                        datalabels: {
                            color: '#fff',
                            formatter: function (value, context) {
                                const data = context.chart.data.datasets[0].data;
                                const total = data.reduce((sum, val) => sum + (val >= 0 ? val : 0), 0);
                                const percent = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                                console.log(`Role chart label - Value: ${value}, Total: ${total}, Percent: ${percent}`);
                                return total > 0 ? `${value} (${percent}%)` : `0 (0%)`;
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
                    console.log('Fetching user role stats');
                    const response = await fetch(url, { cache: 'no-store' });
                    if (!response.ok) {
                        throw new Error(`API error: ${response.status} ${response.statusText}`);
                    }
                    const data = await response.json();
                    console.log('Raw user role stats data:', data);

                    if (!Array.isArray(data)) {
                        throw new Error('User role stats data is not an array');
                    }

                    const validData = data.filter(item => item.count >= 0 && typeof item.count === 'number');
                    const labels = validData.map(item => item.role || 'Unknown');
                    const counts = validData.map(item => item.count || 0);
                    console.log('Role chart labels:', labels);
                    console.log('Role chart counts:', counts);

                    userRoleChart.data.labels = labels;
                    userRoleChart.data.datasets[0].data = counts;
                    userRoleChart.update();
                    console.log('Role chart updated');
                } catch (error) {
                    console.error('Error fetching user role stats:', error);
                    alert('Failed to load role chart data. Check console for details.');
                    userRoleChart.data.labels = ['Error'];
                    userRoleChart.data.datasets[0].data = [0];
                    userRoleChart.update();
                }
            }

            // Initialize both charts
            document.addEventListener('DOMContentLoaded', () => {
                fetchChartData('month');
                loadChartData();
            });

            // Update bar chart on period change
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
