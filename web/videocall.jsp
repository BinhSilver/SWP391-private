<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SWP_HUY | Video Call</title>
        <meta name="current-user-id" content="${sessionScope.authUser.userID}">
        <meta name="current-username" content="${sessionScope.authUser.fullName}">
        <meta name="current-email" content="${sessionScope.authUser.email}">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://unpkg.com/@daily-co/daily-js"></script>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>">
        <style>
            body {
                font-family: 'JetBrains Mono', monospace;
                background: #f0f0f0;
                text-align: center;
    
            }
            .video-call-container {
                max-width: 600px;
                margin: 0 auto;
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
                margin-top: 20px;
            }
            #videoCallContent {
                margin-top: 20px;
            }
        </style>

    </head>
    <body>
        <%@ include file="Home/nav.jsp" %>
        <c:if test="${empty sessionScope.authUser}">
            <c:redirect url="/SWP_HUY/LoginJSP/LoginIndex.jsp"/>
        </c:if>

        <div class="video-call-container">
            <h2>Video Call</h2>
            <button onclick="createRoom()" style="padding: 10px 20px; background: #FA9DC8; color: white; border: none; border-radius: 5px; margin-right: 10px;">Tạo Phòng</button>
            <div style="display: inline-block;">
                <input type="text" id="roomCode" placeholder="Nhập mã phòng" style="padding: 5px; margin-bottom: 10px;">
                <button onclick="joinRoom()" style="padding: 10px 20px; background: #f488ad; color: white; border: none; border-radius: 5px;">Tham Gia</button>
            </div>
            <div id="videoCallContent"></div>
        </div>

        <!-- Khởi tạo biến JavaScript từ session -->
        <script>
        var userEmail = "${sessionScope.authUser.email}";
        var userPassword = "${sessionScope.authUser.passwordHash}";
        if (!userPassword) {
            console.error("Password not found in session. Please ensure login sets it.");
            alert("Không thể tải token do thiếu thông tin xác thực. Vui lòng đăng nhập lại.");
            window.location.href = "/SWP_HUY/LoginJSP/LoginIndex.jsp";
        }
        </script>
        <script src="${pageContext.request.contextPath}/js/videocall.js"></script>
        
        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>