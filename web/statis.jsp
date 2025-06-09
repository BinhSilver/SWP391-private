<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
        <title>Wasabii Statistics</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
        <script src="https://cdn.tailwindcss.com"></script>
        <link rel="stylesheet" href="css/statiscss.css">
    </head>
    <body>
        <!-- Sidebar -->
        <div class="flex min-h-screen">
            <aside class="w-64 bg-white shadow-md p-6 space-y-6">
                <div class="text-xl font-bold">Cổng Quản trị</div>
                <nav class="space-y-3 text-gray-700 font-medium">
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>🏠</span><span>Bảng điều khiển</span></a>
                    <a href="userManagement" class="flex items-center space-x-2 hover:text-blue-500"><span>👤</span><span>Người dùng</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>📚</span><span>Khóa học</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>💰</span><span>Doanh thu</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>📄</span><span>Báo cáo</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>⚙️</span><span>Cài đặt</span></a>
                    <a href="#" class="flex items-center space-x-2 hover:text-blue-500"><span>❓</span><span>Trợ giúp</span></a>
                </nav>
            </aside>

            <!-- Main Dashboard -->
            <main class="flex-1 p-6 space-y-6">
                <!-- Top bar -->
                <div class="flex justify-between items-center">
                    <h1 class="text-2xl font-bold">Tổng quan Dashboard</h1>
                    <div class="flex items-center space-x-2">
                        <span><%= user.getFullName() %></span>
                        <img src="https://bizweb.dktcdn.net/100/429/539/files/dffhg7q-78fd4c03-1213-4106-b1cc-fa812749f930-jpeg.jpg?v=1679825620651" alt="avatar" class="w-10 h-10 rounded-full">
                    </div>
                </div>

                <!-- Cards -->
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Tổng người dùng</div>
                        <div class="text-2xl font-bold" id="totalUsers">Đang tải...</div>
                        <div class="text-green-500 text-sm" id="userGrowthRate">Đang tải...</div>
                    </div>
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Người dùng Premium</div>
                        <div class="text-2xl font-bold" id="currentMonthPremium">Đang tải...</div>
                        <div class="text-green-500 text-sm" id="premiumGrowthRate">Đang tải...</div>
                    </div>
                    <div class="bg-white p-4 rounded-lg shadow">
                        <div class="text-sm text-gray-500">Tổng khóa học</div>
                        <div class="text-2xl font-bold" id="totalCourses">Đang tải...</div>
                        <div class="text-green-500 text-sm" id="courseGrowthRate">Đang tải...</div>
                    </div>
                </div>

                <script>
                    // Fetch data for cards
                    function fetchCardData() {
                        const url = `<%= request.getContextPath() %>/UserStatsServlet`;
                        console.log('Fetching card data from:', url); // Ghi log để debug

                        fetch(url)
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error(`Lỗi HTTP: ${response.status} ${response.statusText}`);
                                }
                                return response.json();
                            })
                            .then(data => {
                                console.log('Dữ liệu thẻ nhận được:', data); // Ghi log dữ liệu
                                // Cập nhật thẻ Tổng người dùng
                                document.getElementById('totalUsers').textContent = data.totalUsers ? data.totalUsers.toLocaleString() : '0';
                                document.getElementById('userGrowthRate').textContent = 
                                    data.userGrowthRate !== undefined ? `${data.userGrowthRate >= 0 ? '+' : ''}${data.userGrowthRate.toFixed(1)}% so với tháng trước` : 'N/A';

                                // Cập nhật thẻ Người dùng Premium
                                document.getElementById('currentMonthPremium').textContent = data.currentMonthPremium ? data.currentMonthPremium.toLocaleString() : '0';
                                document.getElementById('premiumGrowthRate').textContent = 
                                    data.premiumGrowthRate !== undefined ? `${data.premiumGrowthRate >= 0 ? '+' : ''}${data.premiumGrowthRate.toFixed(1)}% so với tháng trước` : 'N/A';

                                // Cập nhật thẻ Tổng khóa học
                                document.getElementById('totalCourses').textContent = data.totalCourses ? data.totalCourses.toLocaleString() : '0';
                                document.getElementById('courseGrowthRate').textContent = 
                                    data.courseGrowthRate !== undefined ? `${data.courseGrowthRate >= 0 ? '+' : ''}${data.courseGrowthRate.toFixed(1)}% so với tháng trước` : 'N/A';
                            })
                            .catch(error => {
                                console.error('Lỗi khi lấy dữ liệu thẻ:', error);
                                document.getElementById('totalUsers').textContent = 'Lỗi';
                                document.getElementById('userGrowthRate').textContent = 'Lỗi';
                                document.getElementById('currentMonthPremium').textContent = 'Lỗi';
                                document.getElementById('premiumGrowthRate').textContent = 'Lỗi';
                                document.getElementById('totalCourses').textContent = 'Lỗi';
                                document.getElementById('courseGrowthRate').textContent = 'Lỗi';
                            });
                    }

                    // Gọi hàm lấy dữ liệu khi trang tải
                    window.onload = function() {
                        fetchCardData();
                    };
                </script>

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
            </main>
        </div>
    </body>
</html>