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
                    <a href="courseManagement" class="flex items-center space-x-2 hover:text-blue-500"><span>📚</span><span>Khóa học</span></a>
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
                        <span><%= user.getFullName()%></span>
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
                        const url = '<%= request.getContextPath()%>/UserStatsServlet';
                        console.log('Fetching card data from:' + url);
                        fetch(url)
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Lỗi HTTP: ' + response.status + ' ' + response.statusText);
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    console.log('Dữ liệu thẻ nhận được:' + JSON.stringify(data));
                                    document.getElementById('totalUsers').textContent = data.totalUsers ? data.totalUsers.toLocaleString() : '0';
                                    document.getElementById('userGrowthRate').textContent =
                                            data.userGrowthRate !== undefined ? (data.userGrowthRate >= 0 ? '+' : '') + data.userGrowthRate.toFixed(1) + '% so với tháng trước' : 'N/A';
                                    document.getElementById('currentMonthPremium').textContent = data.currentMonthPremium ? data.currentMonthPremium.toLocaleString() : '0';
                                    document.getElementById('premiumGrowthRate').textContent =
                                            data.premiumGrowthRate !== undefined ? (data.premiumGrowthRate >= 0 ? '+' : '') + data.premiumGrowthRate.toFixed(1) + '% so với tháng trước' : 'N/A';
                                    document.getElementById('totalCourses').textContent = data.totalCourses ? data.totalCourses.toLocaleString() : '0';
                                    document.getElementById('courseGrowthRate').textContent =
                                            data.courseGrowthRate !== undefined ? (data.courseGrowthRate >= 0 ? '+' : '') + data.courseGrowthRate.toFixed(1) + '% so với tháng trước' : 'N/A';
                                })
                                .catch(error => {
                                    console.error('Lỗi khi lấy dữ liệu thẻ:' + error);
                                    document.getElementById('totalUsers').textContent = 'Lỗi';
                                    document.getElementById('userGrowthRate').textContent = 'Lỗi';
                                    document.getElementById('currentMonthPremium').textContent = 'Lỗi';
                                    document.getElementById('premiumGrowthRate').textContent = 'Lỗi';
                                    document.getElementById('totalCourses').textContent = 'Lỗi';
                                    document.getElementById('courseGrowthRate').textContent = 'Lỗi';
                                });
                    }

                    window.onload = function () {
                        fetchCardData();
                    };
                </script>

                <!-- Charts and Users -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <!-- Charts (2/3) -->
                    <div class="lg:col-span-2 space-y-6">
                        <!-- Course Enrollments Chart -->
                        <div class="bg-white p-4 rounded-lg shadow">
                            <div class="flex justify-between items-center mb-2">
                                <h2 class="text-lg font-semibold">Course Enrollments</h2>
                                <div>
                                    <button id="enrollmentMonthBtn" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">Month</button>
                                    <button id="enrollmentYearBtn" class="bg-blue-500 text-white px-4 py-2 rounded">Year</button>
                                </div>
                            </div>
                            <canvas id="enrollmentChart" height="150"></canvas>
                        </div>

                        <!-- User Registrations Chart -->
                        <div class="bg-white p-4 rounded-lg shadow">
                            <div class="flex justify-between items-center mb-2">
                                <h2 class="text-lg font-semibold">User Registrations</h2>
                                <div>
                                    <button id="registrationMonthBtn" class="bg-blue-500 text-white px-4 py-2 rounded mr-2">Month</button>
                                    <button id="registrationYearBtn" class="bg-blue-500 text-white px-4 py-2 rounded">Year</button>
                                </div>
                            </div>
                            <canvas id="registrationChart" height="150"></canvas>
                        </div>
                    </div>

                    <!-- Recent Users (1/3) -->
                    <div class="bg-white p-4 rounded-lg shadow" style="max-width: 500px; height: 800px;">
                        <div class="flex justify-between items-center mb-2">
                            <h2 class="text-lg font-semibold">Recent Users</h2>
                            <input type="text" id="searchUser" placeholder="Search users..." class="px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 w-1/2" />
                        </div>
                        <div id="userList" class="space-y-4 overflow-y-auto" style="height: calc(100% - 60px);">
                            <!-- User list will be populated here -->
                        </div>
                    </div>

                    <script>
                        async function fetchUsers() {
                            try {
                                const response = await fetch('<%= request.getContextPath()%>/api/users');
                                const users = await response.json();
                                return users.slice(0, 10);
                            } catch (error) {
                                console.error('Error fetching users:', error);
                                return [];
                            }
                        }

                        function renderUsers(users, searchQuery = '') {
                            const userList = document.getElementById('userList');
                            userList.innerHTML = '';

                            const filteredUsers = users.filter(user =>
                                user.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                                        user.email.toLowerCase().includes(searchQuery.toLowerCase())
                            );

                            filteredUsers.forEach(user => {
                                const userDiv = document.createElement('div');
                                userDiv.className = 'flex items-center justify-between py-2 border-t';

                                let roleClass, roleLabel;
                                if (user.roleID == 2) {
                                    roleClass = 'bg-yellow-100 text-yellow-800';
                                    roleLabel = 'Premium';
                                } else if (user.roleID == 3) {
                                    roleClass = 'bg-blue-100 text-blue-800';
                                    roleLabel = 'Teacher';
                                } else {
                                    roleClass = 'bg-gray-100 text-gray-800';
                                    roleLabel = 'Free';
                                }

                                let statusClass, statusLabel;
                                if (user.isActive && !user.isLocked) {
                                    statusClass = 'bg-green-100 text-green-800';
                                    statusLabel = 'Active';
                                } else if (!user.isActive) {
                                    statusClass = 'bg-red-100 text-red-800';
                                    statusLabel = 'Inactive';
                                } else {
                                    statusClass = 'bg-orange-100 text-orange-800';
                                    statusLabel = 'Suspended';
                                }

                                userDiv.innerHTML =
                                        '<div class="flex items-center space-x-2">' +
                                        '<img src="' + (user.avatar ? '<%= request.getContextPath()%>/' + user.avatar : 'https://via.placeholder.com/40') + '" alt="Avatar" class="w-10 h-10 rounded-full">' +
                                        '<div>' +
                                        '<p class="font-medium text-sm">' + user.fullName + '</p>' +
                                        '<p class="text-xs text-gray-500">' + user.email + '</p>' +
                                        '</div>' +
                                        '</div>' +
                                        '<div class="flex items-center space-x-2">' +
                                        '<span class="px-2 py-1 rounded text-xs ' + roleClass + '">' + roleLabel + '</span>' +
                                        '<span class="px-2 py-1 rounded text-xs ' + statusClass + '">' + statusLabel + '</span>' +
                                        (user.isActive && !user.isLocked ?
                                            '<form action="<%= request.getContextPath()%>/userManagement" method="post" style="display:inline;">' +
                                            '<input type="hidden" name="userId" value="' + user.userID + '">' +
                                            '<input type="hidden" name="action" value="block">' +
                                            '<button type="submit" onclick="return confirm(\'Bạn có chắc chắn muốn khóa người dùng này?\')" class="text-red-600 text-xs">Block</button>' +
                                            '</form>'
                                            : user.isLocked ?
                                            '<form action="<%= request.getContextPath()%>/userManagement" method="post" style="display:inline;">' +
                                            '<input type="hidden" name="userId" value="' + user.userID + '">' +
                                            '<input type="hidden" name="action" value="active">' +
                                            '<button type="submit" onclick="return confirm(\'Bạn có chắc chắn muốn kích hoạt người dùng này?\')" class="text-green-600 text-xs">Active</button>' +
                                            '</form>'
                                            : ''
                                        ) +
                                        '</div>';

                                userList.appendChild(userDiv);
                            });
                        }

                        async function fetchAndRenderUsers(searchQuery = '') {
                            const users = await fetchUsers();
                            renderUsers(users, searchQuery);
                        }

                        document.getElementById('searchUser').addEventListener('input', (e) => {
                            const searchQuery = e.target.value;
                            fetchAndRenderUsers(searchQuery);
                        });

                        fetchAndRenderUsers();
                    </script>
                </div>

                <script>
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

                    async function fetchChartData(chartType, period) {
                        try {
                            const contextPath = '<%= request.getContextPath()%>';
                            let url = '';
                            if (chartType === 'enrollment') {
                                url = 'api/enrollments?period=' + period;
                            } else if (chartType === 'registration') {
                                url = 'api/registrations?period=' + period;
                            }
                            const response = await fetch(contextPath + '/' + url);
                            if (!response.ok) {
                                throw new Error('API error: ' + response.status + ' ' + response.statusText);
                            }
                            const data = await response.json();
                            console.log(chartType + ' Data:' + JSON.stringify(data));
                            let chart;
                            if (chartType === 'enrollment') {
                                chart = enrollmentChart;
                            } else if (chartType === 'registration') {
                                chart = registrationChart;
                            }
                            chart.data.labels = data.map(item => item.period);
                            chart.data.datasets[0].data = data.map(item => item.count);
                            chart.update();
                        } catch (error) {
                            console.error('Error fetching ' + chartType + ' data:' + error.message);
                            alert('Không thể tải dữ liệu ' + chartType + '. Vui lòng kiểm tra console.');
                        }
                    }

                    fetchChartData('enrollment', 'month');
                    fetchChartData('registration', 'month');

                    document.getElementById('enrollmentMonthBtn').addEventListener('click', function () {
                        fetchChartData('enrollment', 'month');
                    });

                    document.getElementById('enrollmentYearBtn').addEventListener('click', function () {
                        fetchChartData('enrollment', 'year');
                    });

                    document.getElementById('registrationMonthBtn').addEventListener('click', function () {
                        fetchChartData('registration', 'month');
                    });

                    document.getElementById('registrationYearBtn').addEventListener('click', function () {
                        fetchChartData('registration', 'year');
                    });
                </script>
            </main>
        </div>
    </body>
</html>