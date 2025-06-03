<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.*, model.User" %>
<%
    HttpSession currentSession = request.getSession(false);
    User user = (currentSession != null) ? (User) currentSession.getAttribute("authUser") : null;
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán thành công</title>
    <link rel="stylesheet" href="../css/paymentSuccess.css">
</head>
<body>
<div class="success-container">
    <h1> Thanh toán thành công!</h1>
    <p>
        Cảm ơn <strong><%= user != null ? user.getUserID() : "bạn" %></strong>, tài khoản của bạn đã được nâng cấp.
    </p>
    <a href="/Edit/index.jsp">Về trang chính</a>
</div>
</body>
</html>
