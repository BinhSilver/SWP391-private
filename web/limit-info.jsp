<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thông tin giới hạn - Wasabii</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'JetBrains Mono', 'Fira Code', monospace;
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 40px;
            max-width: 600px;
            width: 100%;
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            color: #333;
            font-size: 2.5em;
            margin-bottom: 10px;
        }

        .header p {
            color: #666;
            font-size: 1.1em;
        }

        .status-card {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }

        .status-card.premium {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
        }

        .status-card.free {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
        }

        .status-icon {
            font-size: 3em;
            margin-bottom: 15px;
        }

        .status-title {
            font-size: 1.5em;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .status-description {
            font-size: 1.1em;
            opacity: 0.9;
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .feature-card {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 15px;
            border-left: 5px solid #e94f64;
            transition: transform 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-5px);
        }

        .feature-card.enabled {
            border-left-color: #e94f64;
            background: #fff5f6;
        }

        .feature-card.disabled {
            border-left-color: #dc3545;
            background: #f8d7da;
        }

        .feature-icon {
            font-size: 2em;
            margin-bottom: 15px;
            color: #e94f64;
        }

        .feature-card.enabled .feature-icon {
            color: #e94f64;
        }

        .feature-card.disabled .feature-icon {
            color: #dc3545;
        }

        .feature-title {
            font-size: 1.2em;
            font-weight: bold;
            margin-bottom: 10px;
            color: #333;
        }

        .feature-description {
            color: #666;
            line-height: 1.5;
        }

        .upgrade-section {
            text-align: center;
            padding: 30px;
            background: linear-gradient(135deg, #fff5f6 0%, #ffe6e9 100%);
            border-radius: 15px;
            margin-top: 30px;
            border: 2px solid #e94f64;
        }

        .upgrade-title {
            font-size: 1.5em;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
        }

        .upgrade-description {
            color: #666;
            margin-bottom: 20px;
            line-height: 1.6;
        }

        .upgrade-btn {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            color: white;
            border: none;
            padding: 15px 30px;
            border-radius: 25px;
            font-size: 1.1em;
            font-weight: bold;
            cursor: pointer;
            transition: transform 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }

        .upgrade-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(233, 79, 100, 0.3);
        }

        .back-btn {
            background: #e94f64;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 20px;
            font-size: 1em;
            cursor: pointer;
            transition: background 0.3s ease;
            text-decoration: none;
            display: inline-block;
            margin-top: 20px;
        }

        .back-btn:hover {
            background: #d63384;
        }

        .limit-info {
            background: #fff5f6;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            border-left: 5px solid #e94f64;
        }

        .limit-info h3 {
            color: #e94f64;
            margin-bottom: 10px;
        }

        .limit-info p {
            color: #424242;
            line-height: 1.5;
        }
        
                .premium-benefits {
            background: rgba(255, 255, 255, 0.9);
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
            border: 1px solid #e94f64;
        }

        .premium-benefits h5 {
            color: #e94f64;
            margin-bottom: 15px;
            font-weight: bold;
        }

        .benefits-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .benefits-list li {
            padding: 8px 0;
            display: flex;
            align-items: center;
            gap: 10px;
            color: #333;
        }

        .benefits-list li i {
            width: 20px;
            text-align: center;
        }

        .benefits-list li:last-child {
            font-weight: bold;
            color: #e94f64;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1><i class="fas fa-chart-line"></i> Thông tin giới hạn</h1>
            <p>Xem trạng thái tài khoản và các tính năng có sẵn</p>
        </div>
        
        <!-- Error Message -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert" style="margin-bottom: 20px;">
                <i class="fas fa-exclamation-triangle"></i>
                <strong>Lỗi truy cập:</strong> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Status Card -->
        <div class="status-card ${isPremium ? 'premium' : 'free'}">
            <div class="status-icon">
                <i class="fas ${isPremium ? 'fa-crown' : 'fa-user'}"></i>
            </div>
            <div class="status-title">
                <c:choose>
                    <c:when test="${sessionScope.authUser.roleID == 3}">Teacher</c:when>
                    <c:when test="${sessionScope.authUser.roleID == 4}">Admin</c:when>
                    <c:when test="${isPremium}">Premium User</c:when>
                    <c:otherwise>Free User</c:otherwise>
                </c:choose>
            </div>
            <div class="status-description">
                ${limitInfo}
            </div>
        </div>

        <!-- Limit Information -->
        <div class="limit-info">
            <h3><i class="fas fa-info-circle"></i> Giới hạn hiện tại</h3>
            <c:choose>
                <c:when test="${sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4}">
                    <p><strong>Flashcard:</strong> Không giới hạn (Teacher/Admin)</p>
                    <p><strong>Item trong flashcard:</strong> Không giới hạn (Teacher/Admin)</p>
                    <p><strong>Video Call:</strong> Có thể sử dụng (Teacher/Admin)</p>
                    <p><strong>AI Call:</strong> Có thể sử dụng (Teacher/Admin)</p>
                    <p><strong>Quảng cáo:</strong> Không có quảng cáo (Teacher/Admin)</p>
                </c:when>
                <c:otherwise>
                    <p><strong>Flashcard:</strong> ${isPremium ? 'Không giới hạn' : '2 flashcard/tuần'}</p>
                    <p><strong>Item trong flashcard:</strong> ${isPremium ? 'Không giới hạn' : '10 item/flashcard'}</p>
                    <p><strong>Video Call:</strong> ${canUseVideoCall ? 'Có thể sử dụng' : 'Chỉ Premium'}</p>
                    <p><strong>AI Call:</strong> ${canUseAICall ? 'Có thể sử dụng' : 'Chỉ Premium'}</p>
                    <p><strong>Quảng cáo:</strong> ${isPremium ? 'Không có quảng cáo' : 'Có quảng cáo'}</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Features Grid -->
        <div class="features-grid">
            <div class="feature-card ${canUseVideoCall ? 'enabled' : 'disabled'}">
                <div class="feature-icon">
                    <i class="fas fa-video"></i>
                </div>
                <div class="feature-title">Video Call</div>
                <div class="feature-description">
                    <c:choose>
                        <c:when test="${sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4}">
                            Bạn có thể sử dụng tính năng video call với giáo viên và học viên khác. (Teacher/Admin)
                        </c:when>
                        <c:otherwise>
                            ${canUseVideoCall ? 'Bạn có thể sử dụng tính năng video call với giáo viên và học viên khác.' : 'Tính năng này chỉ dành cho Premium User. Nâng cấp để sử dụng.'}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="feature-card ${canUseAICall ? 'enabled' : 'disabled'}">
                <div class="feature-icon">
                    <i class="fas fa-robot"></i>
                </div>
                <div class="feature-title">AI Call</div>
                <div class="feature-description">
                    <c:choose>
                        <c:when test="${sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4}">
                            Bạn có thể sử dụng tính năng AI call để luyện tập với AI. (Teacher/Admin)
                        </c:when>
                        <c:otherwise>
                            ${canUseAICall ? 'Bạn có thể sử dụng tính năng AI call để luyện tập với AI.' : 'Tính năng này chỉ dành cho Premium User. Nâng cấp để sử dụng.'}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="feature-card enabled">
                <div class="feature-icon">
                    <i class="fas fa-layer-group"></i>
                </div>
                <div class="feature-title">Flashcard</div>
                <div class="feature-description">
                    <c:choose>
                        <c:when test="${sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4}">
                            Tạo không giới hạn flashcard và item. (Teacher/Admin)
                        </c:when>
                        <c:otherwise>
                            ${isPremium ? 'Tạo không giới hạn flashcard và item.' : 'Tạo tối đa 2 flashcard/tuần và 10 item/flashcard.'}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="feature-card enabled">
                <div class="feature-icon">
                    <i class="fas fa-book"></i>
                </div>
                <div class="feature-title">Khóa học</div>
                <div class="feature-description">
                    Truy cập tất cả khóa học và bài học có sẵn.
                </div>
            </div>
            
            <div class="feature-card ${isPremium || sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4 ? 'enabled' : 'disabled'}">
                <div class="feature-icon">
                    <i class="fas fa-ban"></i>
                </div>
                <div class="feature-title">Không quảng cáo</div>
                <div class="feature-description">
                    <c:choose>
                        <c:when test="${sessionScope.authUser.roleID == 3 || sessionScope.authUser.roleID == 4}">
                            Trải nghiệm học tập không bị gián đoạn bởi quảng cáo. (Teacher/Admin)
                        </c:when>
                        <c:otherwise>
                            ${isPremium ? 'Trải nghiệm học tập không bị gián đoạn bởi quảng cáo.' : 'Loại bỏ tất cả quảng cáo khi nâng cấp lên Premium.'}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Upgrade Section (chỉ hiển thị cho Free User) -->
        <c:if test="${!isPremium && sessionScope.authUser.roleID != 3 && sessionScope.authUser.roleID != 4}">
            <div class="upgrade-section">
                <div class="upgrade-title">
                    <i class="fas fa-star"></i> Nâng cấp lên Premium
                </div>
                <div class="upgrade-description">
                    Nâng cấp tài khoản để mở khóa tất cả tính năng và không còn giới hạn!
                </div>
                
                <!-- Premium Benefits -->
                <div class="premium-benefits">
                    <h5><i class="fas fa-check-circle text-success"></i> Lợi ích Premium:</h5>
                    <ul class="benefits-list">
                        <li><i class="fas fa-video text-primary"></i> Video Call không giới hạn</li>
                        <li><i class="fas fa-robot text-primary"></i> AI Call để luyện tập</li>
                        <li><i class="fas fa-layer-group text-primary"></i> Flashcard không giới hạn</li>
                        <li><i class="fas fa-ban text-danger"></i> <strong>Không có quảng cáo</strong></li>
                    </ul>
                </div>
                
                <a href="${pageContext.request.contextPath}/payment" class="upgrade-btn">
                    <i class="fas fa-arrow-up"></i> Nâng cấp ngay
                </a>
            </div>
        </c:if>

        <!-- Back Button -->
        <div style="text-align: center;">
            <a href="${pageContext.request.contextPath}/HomeServlet" class="back-btn">
                <i class="fas fa-home"></i> Về trang chủ
            </a>
        </div>
    </div>

    <script>
        // Thêm hiệu ứng animation cho các card
        document.addEventListener('DOMContentLoaded', function() {
            const cards = document.querySelectorAll('.feature-card, .status-card');
            cards.forEach((card, index) => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    card.style.transition = 'all 0.5s ease';
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, index * 100);
            });
        });
    </script>
</body>
</html> 