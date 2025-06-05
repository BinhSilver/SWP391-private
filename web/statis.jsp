<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Wasabii Statistics</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <script src="https://cdn.tailwindcss.com"></script>
        <link rel="stylesheet" href="css/statiscss.css">
    </head>
    <body >

        <!-- Sidebar -->
        <div class="flex min-h-screen">
            <aside class="w-64 bg-white shadow-md p-6 space-y-6">
                <div class="text-xl font-bold">Admin Portal</div>
                <nav class="space-y-3 text-gray-700 font-medium">
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>🏠</span><span>Dashboard</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>👤</span><span>Users</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>📚</span><span>Courses</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>💰</span><span>Revenue</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>📄</span><span>Reports</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>⚙️</span><span>Settings</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>❓</span><span>Help</span></a>
                </nav>
            </aside>

            <!-- Main Dashboard -->
            <main class="flex-1 p-6 space-y-6">
                <!-- Top bar -->
                <div class="flex justify-between items-center">
                    <h1 class="text-2xl font-bold">Dashboard Overview</h1>
                    <div class="flex space-x-4 items-center">
                        <div>
                            <label for="month" class="block text-sm font-medium text-gray-700">Month</label>
                            <select id="month" name="month" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                                <option value="1">January</option>
                                <option value="2">February</option>
                                <option value="3">March</option>
                                <option value="4">April</option>
                                <option value="5">May</option>
                                <option value="6">June</option>
                                <option value="7">July</option>
                                <option value="8">August</option>
                                <option value="9">September</option>
                                <option value="10">October</option>
                                <option value="11">November</option>
                                <option value="12">December</option>
                            </select>
                        </div>
                        <div>
                            <label for="year" class="block text-sm font-medium text-gray-700">Year</label>
                            <select id="year" name="year" class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-md">
                                <%
                                    int currentYear = java.time.Year.now().getValue();
                                    for (int i = currentYear; i >= 2020; i--) {
                                %>
                                <option value="<%= i%>"><%= i%></option>
                                <% }%>
                            </select>
                        </div>
                        <div class="pt-5">
                            <button onclick="fetchChartData()" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition">Apply</button>
                        </div>
                    </div>
                    <div class="flex items-center space-x-2">
                        <span><%= user.getFullName()%></span>
                        <img src="https://bizweb.dktcdn.net/100/429/539/files/dffhg7q-78fd4c03-1213-4106-b1cc-fa812749f930-jpeg.jpg?v=1679825620651" alt="avatar" class="w-10 h-10 rounded-full">
                    </div>
                </div>

                <!-- Cards -->
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Total Users</div>
                        <div class="text-2xl font-bold">12,543</div>
                        <div class="text-green-500 text-sm">+12.5% from last month</div>
                    </div>
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Active Courses</div>
                        <div class="text-2xl font-bold">89</div>
                        <div class="text-green-500 text-sm">+8.2% from last month</div>
                    </div>
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Revenue</div>
                        <div class="text-2xl font-bold">$54,320</div>
                        <div class="text-green-500 text-sm">+15.3% from last month</div>
                    </div>
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Completion Rate</div>
                        <div class="text-2xl font-bold">87.3%</div>
                        <div class="text-green-500 text-sm">+3.1% from last month</div>
                    </div>
                </div>

                <!-- Charts and Users -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <!-- Charts (2/3) -->

                    <div class="lg:col-span-2 space-y-6">
                        <div class="bg-white p-4 rounded-lg shadow">
                            <h2 class="text-lg font-semibold mb-2">Course Enrollments</h2>
                            <canvas id="enrollmentChart" height="150"></canvas>
                        </div>
                        <div class="bg-white p-4 rounded-lg shadow">
                            <h2 class="text-lg font-semibold mb-2">User Registrations</h2>
                            <canvas id="registrationChart" height="150"></canvas>
                        </div>
                    </div>

                    <!-- Recent Users (1/3) -->
                    <!-- Recent Users -->
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="flex justify-between items-center mb-2">
                            <h2 class="text-lg font-semibold">Recent Users</h2>
                            <input type="text" id="searchUser" placeholder="Search users..." class="px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" />
                        </div>
                        <div id="userList" class="space-y-4">
                            <!-- Sample Users -->
                            <div class="flex items-center justify-between">
                                <div>
                                    <p class="font-medium">John Smith</p>
                                    <p class="text-sm text-gray-500">john@example.com</p>
                                </div>
                                <div class="flex items-center space-x-2">
                                    <span class="px-2 py-1 bg-green-100 text-green-700 rounded text-sm">Active</span>
                                    <button class="px-2 py-1 bg-red-100 text-red-700 rounded text-sm">Block</button>
                                </div>
                            </div>
                            <div class="flex items-center justify-between">
                                <div>
                                    <p class="font-medium">Emma Wilson</p>
                                    <p class="text-sm text-gray-500">emma@example.com</p>
                                </div>
                                <div class="flex items-center space-x-2">
                                    <span class="px-2 py-1 bg-green-100 text-green-700 rounded text-sm">Active</span>
                                    <button class="px-2 py-1 bg-red-100 text-red-700 rounded text-sm">Block</button>
                                </div>
                            </div>
                            <div class="flex items-center justify-between">
                                <div>
                                    <p class="font-medium">Mike Johnson</p>
                                    <p class="text-sm text-gray-500">mike@example.com</p>
                                </div>
                                <div class="flex items-center space-x-2">
                                    <span class="px-2 py-1 bg-yellow-100 text-yellow-700 rounded text-sm">Pending</span>
                                    <button class="px-2 py-1 bg-red-100 text-red-700 rounded text-sm">Block</button>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>


        <script>
            // Initialize charts
            const enrollmentCtx = document.getElementById('enrollmentChart').getContext('2d');
            const registrationCtx = document.getElementById('registrationChart').getContext('2d');

            const enrollmentChart = new Chart(enrollmentCtx, {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [{
                            label: 'Course Enrollments',
                            data: [],
                            backgroundColor: 'rgba(75, 192, 192, 0.5)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1
                        }]
                },
                options: {
                    scales: {
                        y: {beginAtZero: true, title: {display: true, text: 'Number of Enrollments'}},
                        x: {title: {display: true, text: 'Time Period'}}
                    }
                }
            });

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
                        y: {beginAtZero: true, title: {display: true, text: 'Number of Registrations'}},
                        x: {title: {display: true, text: 'Time Period'}}
                    }
                }
            });

            // Fetch and update chart data
            async function fetchChartData(period) {
                try {
                    const contextPath = '<%= request.getContextPath()%>';
                    // Fetch enrollment data
                    const enrollmentResponse = await fetch(`api/enrollments?period=${period}`);
                    if (!enrollmentResponse.ok) {
                        throw new Error(`Enrollment API error: ${enrollmentResponse.status} ${enrollmentResponse.statusText}`);
                    }
                    const enrollmentData = await enrollmentResponse.json();
                    console.log('Enrollment Data:', enrollmentData);
                    enrollmentChart.data.labels = enrollmentData.map(item => item.period);
                    enrollmentChart.data.datasets[0].data = enrollmentData.map(item => item.count);
                    enrollmentChart.update();

                    // Fetch registration data
                    const registrationResponse = await fetch(`api/registrations?period=${period}`);
                    if (!registrationResponse.ok) {
                        throw new Error(`Registration API error: ${registrationResponse.status} ${registrationResponse.statusText}`);
                    }
                    const registrationData = await registrationResponse.json();
                    console.log('Registration Data:', registrationData);
                    registrationChart.data.labels = registrationData.map(item => item.period);
                    registrationChart.data.datasets[0].data = registrationData.map(item => item.count);
                    registrationChart.update();
                } catch (error) {
                    console.error('Error fetching data:', error.message);
                    alert('Không thể tải dữ liệu biểu đồ. Vui lòng kiểm tra console để biết chi tiết.');
                }
            }

            // Initial data fetch
            fetchChartData('month');

            // Update charts on period change
            document.getElementById('timePeriod').addEventListener('change', function () {
                fetchChartData(this.value);
            });
        </script>
    </body>
</html>