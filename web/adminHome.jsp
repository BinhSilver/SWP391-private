<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Admin Dashboard - Wasabii</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <style>
            .sidebar {
                position: fixed;
                top: 56px;
                bottom: 0;
                left: 0;
                z-index: 100;
                padding: 48px 0 0;
                box-shadow: inset -1px 0 0 rgba(0, 0, 0, .1);
                width: 250px;
                background-color: #f8f9fa;
            }
            
            .main-content {
                margin-left: 250px;
                padding: 2rem;
                margin-top: 56px;
            }
            
            .sidebar-sticky {
                position: relative;
                top: 0;
                height: calc(100vh - 48px);
                padding-top: .5rem;
                overflow-x: hidden;
                overflow-y: auto;
            }
            
            .nav-link {
                color: #333;
                padding: .5rem 1rem;
            }
            
            .nav-link:hover {
                color: #007bff;
            }
            
            .nav-link.active {
                color: #007bff;
                background-color: #e9ecef;
            }
            
            .dashboard-card {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                padding: 1.5rem;
                margin-bottom: 1rem;
            }
            
            .stats-number {
                font-size: 2rem;
                font-weight: bold;
                color: #007bff;
            }
        </style>
    </head>
    <body>
        <jsp:include page="Menu.jsp"></jsp:include>
        
        <!-- Sidebar -->
        <nav class="sidebar">
            <div class="sidebar-sticky">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#">
                            <i class="fas fa-home me-2"></i>
                            Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="admin/premium">
                            <i class="fas fa-users me-2"></i>
                            Quản lý các gói premium
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">
                            <i class="fas fa-chart-line me-2"></i>
                            Statistics
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">
                            <i class="fas fa-cog me-2"></i>
                            Settings
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Main content -->
        <main class="main-content">
            <div class="container-fluid">
                <h1 class="h2 mb-4">Dashboard</h1>
                
                <div class="row">
                    <div class="col-md-3">
                        <div class="dashboard-card">
                            <h5>Total Users</h5>
                            <div class="stats-number">120</div>
                            <p class="mb-0">Active users in system</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="dashboard-card">
                            <h5>Premium Users</h5>
                            <div class="stats-number">45</div>
                            <p class="mb-0">Subscribed users</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="dashboard-card">
                            <h5>Active Calls</h5>
                            <div class="stats-number">8</div>
                            <p class="mb-0">Current video calls</p>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="dashboard-card">
                            <h5>Revenue</h5>
                            <div class="stats-number">$2.5K</div>
                            <p class="mb-0">Monthly revenue</p>
                        </div>
                    </div>
                </div>

                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="dashboard-card">
                            <h5>Recent Activities</h5>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>User</th>
                                            <th>Action</th>
                                            <th>Time</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>John Doe</td>
                                            <td>Upgraded to Premium</td>
                                            <td>2 hours ago</td>
                                        </tr>
                                        <tr>
                                            <td>Jane Smith</td>
                                            <td>Started video call</td>
                                            <td>3 hours ago</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="dashboard-card">
                            <h5>System Status</h5>
                            <div class="table-responsive">
                                <table class="table">
                                    <tbody>
                                        <tr>
                                            <td>Server Status</td>
                                            <td><span class="badge bg-success">Online</span></td>
                                        </tr>
                                        <tr>
                                            <td>Database Status</td>
                                            <td><span class="badge bg-success">Connected</span></td>
                                        </tr>
                                        <tr>
                                            <td>Last Backup</td>
                                            <td>2024-03-15 03:00 AM</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 