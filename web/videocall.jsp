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
        
        <!-- Advertisement Banner -->
        <%@ include file="ads.jsp"%>
        <c:if test="${empty sessionScope.authUser}">
            <c:redirect url="/test/LoginJSP/LoginIndex.jsp"/>
        </c:if>

        <!-- Kiểm tra quyền sử dụng video call -->
        <c:if test="${not empty sessionScope.authUser}">
            <c:set var="premiumService" value="<%= new service.PremiumService() %>" />
            <c:set var="canUseVideoCall" value="${premiumService.canUseVideoCall(sessionScope.authUser.userID)}" />
            
            <c:if test="${!canUseVideoCall}">
                <div class="container mt-5">
                    <div class="row justify-content-center">
                        <div class="col-md-8">
                            <div class="card border-warning">
                                <div class="card-header bg-warning text-dark">
                                    <h4><i class="fas fa-exclamation-triangle"></i> Truy cập bị từ chối</h4>
                                </div>
                                <div class="card-body text-center">
                                    <div class="mb-4">
                                        <i class="fas fa-video-slash" style="font-size: 4rem; color: #ffc107;"></i>
                                    </div>
                                    <h5 class="text-danger mb-3">Tính năng Video Call chỉ dành cho Premium User!</h5>
                                    <p class="text-muted mb-4">
                                        Bạn cần nâng cấp tài khoản lên Premium để sử dụng tính năng Video Call.
                                    </p>
                                    <div class="d-flex justify-content-center gap-3">
                                        <a href="${pageContext.request.contextPath}/HomeServlet" class="btn btn-secondary">
                                            <i class="fas fa-home"></i> Về trang chủ
                                        </a>
                                        <a href="${pageContext.request.contextPath}/limit-info" class="btn btn-info">
                                            <i class="fas fa-info-circle"></i> Xem thông tin giới hạn
                                        </a>
                                        <a href="${pageContext.request.contextPath}/payment" class="btn btn-warning">
                                            <i class="fas fa-crown"></i> Nâng cấp Premium
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <c:if test="${canUseVideoCall}">
        <div class="video-call-container">
            <h2>Video Call</h2>
            <button onclick="createRoom()" style="padding: 10px 20px; background: #FA9DC8; color: white; border: none; border-radius: 5px; margin-right: 10px;">Tạo Phòng</button>
            <div style="display: inline-block;">
                <input type="text" id="roomCode" placeholder="Nhập mã phòng" style="padding: 5px; margin-bottom: 10px;">
                <button onclick="joinRoom()" style="padding: 10px 20px; background: #f488ad; color: white; border: none; border-radius: 5px;">Tham Gia</button>
            </div>
            <div id="videoCallContent"></div>
        </div>
            </c:if>
        </c:if>

        <!-- Khởi tạo biến JavaScript từ session -->
        <script>
            var userEmail = "${sessionScope.authUser.email}";
            var userPassword = "${sessionScope.authUser.passwordHash}";
            if (!userPassword) {
                console.error("Password not found in session. Please ensure login sets it.");
                alert("Không thể tải token do thiếu thông tin xác thực. Vui lòng đăng nhập lại.");
                window.location.href = "/test/LoginJSP/LoginIndex.jsp";
            }
        </script>
        <script>
            // Inject context path vào window object
            window.contextPath = '${pageContext.request.contextPath}';
            console.log('Context path loaded:', window.contextPath);
        </script>
        <script src="${pageContext.request.contextPath}/js/config.js"></script>
        <!-- Các import JS động -->
        <script src="${pageContext.request.contextPath}/js/videocall.js"></script>

        <!-- Scripts -->

        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    </body>
</html>