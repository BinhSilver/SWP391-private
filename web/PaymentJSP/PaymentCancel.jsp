<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../Home/nav.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hủy Thanh Toán</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <div class="alert alert-warning">
                    <h4>Thanh Toán Đã Bị Hủy</h4>
                    <p>${message}</p>
                </div>
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Về Trang Chủ</a>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
