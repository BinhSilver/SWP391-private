<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.*, model.User, model.PremiumPlan" %>
<%
    HttpSession currentSession = request.getSession(false);
    User user = (currentSession != null) ? (User) currentSession.getAttribute("authUser") : null;
    
    // Lấy thông tin gói premium đã mua từ session
    PremiumPlan purchasedPlan = (PremiumPlan) currentSession.getAttribute("purchasedPlan");
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
        
        .plan-info {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
        }
        
        .plan-info h3 {
            color: #007bff;
            margin-bottom: 10px;
            font-size: 18px;
        }
        
        .plan-info p {
            margin: 8px 0;
            color: #6c757d;
        }
        
        .plan-info .price {
            font-weight: bold;
            color: #28a745;
            font-size: 16px;
        }
        
        .plan-info .duration {
            font-weight: bold;
            color: #007bff;
            font-size: 14px;
        }
        
        .benefits {
            background: #e8f5e8;
            border: 1px solid #28a745;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
        }
        
        .benefits h4 {
            color: #28a745;
            margin-bottom: 15px;
            font-size: 16px;
        }
        
        .benefits ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .benefits li {
            margin: 8px 0;
            color: #495057;
            font-size: 14px;
        }
        

    </style>
</head>
<body>
<div class="success-container">
    <div class="success-icon">✓</div>
    <h1>Thanh toán thành công!</h1>
    <p>
        Cảm ơn <strong><%= user != null ? user.getFullName() : "bạn" %></strong>, tài khoản của bạn đã được nâng cấp thành công!
    </p>
    <% if (purchasedPlan != null) { %>
    <div class="plan-info">
        <h3>🎉 Gói Premium: <%= purchasedPlan.getPlanName() %></h3>
        <p><%= purchasedPlan.getDescription() %></p>
        <p class="price">💰 Giá: <%= String.format("%,d", (int)purchasedPlan.getPrice()) %> VNĐ</p>
        <p class="duration">⏰ Thời hạn: <%= purchasedPlan.getDurationInMonths() %> tháng</p>
    </div>
    <% } %>
    <div class="benefits">
        <h4>🎁 Bạn sẽ được hưởng:</h4>
        <ul>
            <li>✅ Truy cập không giới hạn tất cả khóa học</li>
            <li>✅ Tạo flashcard không giới hạn</li>
            <li>✅ Chat với AI không giới hạn</li>
            <li>✅ Không quảng cáo</li>
            <li>✅ Hỗ trợ ưu tiên</li>
        </ul>
    </div>
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
