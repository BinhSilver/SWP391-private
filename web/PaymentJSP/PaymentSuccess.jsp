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
    <style>
        .success-container {
            text-align: center;
            padding: 40px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            max-width: 500px;
            margin: 50px auto;
        }
        
        h1 {
            color: #28a745;
            margin-bottom: 20px;
        }
        
        .countdown {
            font-size: 18px;
            color: #6c757d;
            margin: 20px 0;
        }
        
        .button-container {
            margin-top: 30px;
        }
        
        .home-button {
            display: inline-block;
            padding: 12px 25px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        
        .home-button:hover {
            background-color: #0056b3;
        }
        
        .success-icon {
            font-size: 60px;
            color: #28a745;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="success-container">
    <div class="success-icon">✓</div>
    <h1>Thanh toán thành công!</h1>
    <p>
        Cảm ơn <strong><%= user != null ? user.getUserID() : "bạn" %></strong>, tài khoản của bạn đã được nâng cấp.
    </p>
    <div class="countdown">
        Tự động chuyển về trang chủ sau <span id="timer">10</span> giây
    </div>
    <div class="button-container">
        <a href="${pageContext.request.contextPath}/HomeServlet" class="home-button">Về trang chủ ngay</a>
    </div>
</div>

<script>
    // Countdown timer
    let timeLeft = 10;
    const timerElement = document.getElementById('timer');
    
    const countdown = setInterval(() => {
        timeLeft--;
        timerElement.textContent = timeLeft;
        
        if (timeLeft <= 0) {
            clearInterval(countdown);
            window.location.href = '${pageContext.request.contextPath}/HomeServlet';
        }
    }, 1000);
</script>
</body>
</html>
