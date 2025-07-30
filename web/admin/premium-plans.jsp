<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Quản Lý Gói Premium</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
    <!-- CSS & Fonts -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
</head>
<body>
    <%@ include file="navofadmin.jsp" %>
    <div class="container mt-5">
        <h2>Quản Lý Gói Premium</h2>
        <a href="${pageContext.request.contextPath}/admin/premium-plans?action=add" class="btn btn-primary mb-3">Thêm Gói Mới</a>
        <a href="${pageContext.request.contextPath}/revenue_stats.jsp" class="btn btn-primary mb-3">Thống Kê Doanh Thu</a>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tên Gói</th>
                    <th>Giá (VNĐ)</th>
                    <th>Thời Gian (Tháng)</th>
                    <th>Mô Tả</th>
                    <th>Hành Động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="plan" items="${premiumPlans}">
                    <tr>
                        <td>${plan.planID}</td>
                        <td>${plan.planName}</td>
                        <td>${plan.price}</td>
                        <td>${plan.durationInMonths}</td>
                        <td>${plan.description}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/premium-plans?action=edit&planId=${plan.planID}" class="btn btn-sm btn-warning">Sửa</a>
                            <form action="${pageContext.request.contextPath}/admin/premium-plans" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="planId" value="${plan.planID}">
                                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa gói này không?')">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html> 