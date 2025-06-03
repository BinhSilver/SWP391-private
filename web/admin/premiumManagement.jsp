<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Quản lý Premium - Wasabii</title>
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
            
            .premium-card {
                background: white;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                padding: 1.5rem;
                margin-bottom: 1rem;
            }
            
            .btn-action {
                margin: 0 5px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="../Menu.jsp"></jsp:include>
        
        <!-- Sidebar -->
        <nav class="sidebar">
            <div class="sidebar-sticky">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="adminHome">
                            <i class="fas fa-home me-2"></i>
                            Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="admin/premium">
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
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h1 class="h2">Quản lý Gói Premium</h1>
                    <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addPremiumModal">
                        <i class="fas fa-plus"></i> Thêm Gói Premium
                    </button>
                </div>
                
                <div class="row">
                    <div class="col-12">
                        <div class="premium-card">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Tên Gói</th>
                                            <th>Giá (VNĐ)</th>
                                            <th>Thời Hạn (Tháng)</th>
                                            <th>Mô tả</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${premiumPlans}" var="plan">
                                            <tr>
                                                <td>${plan.planID}</td>
                                                <td>${plan.planName}</td>
                                                <td>${plan.price}</td>
                                                <td>${plan.durationInMonths}</td>
                                                <td>${plan.description}</td>
                                                <td>
                                                    <button class="btn btn-sm btn-primary btn-action" onclick="editPlan(${plan.planID})">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn btn-sm btn-danger btn-action" onclick="deletePlan(${plan.planID})">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>

        <!-- Modal Thêm Gói Premium -->
        <div class="modal fade" id="addPremiumModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Thêm Gói Premium Mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addPremiumForm" action="admin/premium" method="POST">
                            <div class="mb-3">
                                <label class="form-label">Tên Gói</label>
                                <input type="text" class="form-control" name="planName" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Giá (VNĐ)</label>
                                <input type="number" class="form-control" name="price" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Thời Hạn (Tháng)</label>
                                <input type="number" class="form-control" name="duration" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Mô tả</label>
                                <textarea class="form-control" name="description" rows="3"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="submit" form="addPremiumForm" class="btn btn-primary">Thêm</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function editPlan(planId) {
                // Implement edit functionality
                console.log('Edit plan:', planId);
            }
            
            function deletePlan(planId) {
                if(confirm('Bạn có chắc chắn muốn xóa gói premium này?')) {
                    // Implement delete functionality
                    console.log('Delete plan:', planId);
                }
            }
        </script>
    </body>
</html> 