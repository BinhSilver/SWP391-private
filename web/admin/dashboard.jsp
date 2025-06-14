<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <!-- Include Menu -->
    <jsp:include page="../Menu.jsp"></jsp:include>

    <div class="container mt-4">
        <h2>Admin Dashboard</h2>
        
        <div class="row mt-4">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">User Management</h5>
                        <p class="card-text">Manage user accounts, roles, and permissions.</p>
                        <a href="${pageContext.request.contextPath}/userManagement" class="btn btn-primary">Go to User Management</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Premium Management</h5>
                        <p class="card-text">Manage premium plans and subscriptions.</p>
                        <a href="${pageContext.request.contextPath}/premiumManagement" class="btn btn-primary">Go to Premium Management</a>
            </div>
            
            <!-- Add more admin features here -->
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 