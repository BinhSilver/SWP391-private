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
    <title>Thanh toán bị hủy</title>
    <link rel="stylesheet" href="../css/paymentCancel.css">
</head>
<body>
<div class="cancel-container">
    <h1>Thanh toán đã bị hủy</h1>
    <p>
        Rất tiếc <strong><%= user != null ? user.getUserID() : "bạn" %></strong>, giao dịch của bạn chưa được thực hiện.
    </p>
    <a href="/Edit/index.jsp">Về trang chính</a>
</div>
</body>
</html>
