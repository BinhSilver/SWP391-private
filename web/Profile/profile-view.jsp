<%@page import="model.User"%>
<%@page import="model.UserPremium"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page session="true" contentType="text/html" pageEncoding="UTF-8"%>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        user = (User) session.getAttribute("authUser");
    }
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    UserPremium premiumInfo = (UserPremium) request.getAttribute("premiumInfo");
    String avatarUrl = user.getAvatar();
    String defaultAvatar = "assets/avatar/nam.jpg";
    if ("Nữ".equalsIgnoreCase(user.getGender())) {
        defaultAvatar = "assets/avatar/nu.jpg";
    }
    
    // Calculate days remaining for premium
    String premiumStatus = "";
    String premiumStatusClass = "";
    String premiumEndDate = "";
    long daysRemaining = 0;
    
    if (premiumInfo != null && premiumInfo.getEndDate() != null) {
        Date currentDate = new Date();
        Date endDate = premiumInfo.getEndDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        premiumEndDate = sdf.format(endDate);
        
        long diffInMillis = endDate.getTime() - currentDate.getTime();
        daysRemaining = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        
        if (daysRemaining > 0) {
            premiumStatus = "Premium còn " + daysRemaining + " ngày";
            premiumStatusClass = "premium-active";
        } else {
            premiumStatus = "Premium đã hết hạn " + Math.abs(daysRemaining) + " ngày trước";
            premiumStatusClass = "premium-expired";
        }
    } else {
        premiumStatus = "Tài khoản Free";
        premiumStatusClass = "premium-free";
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Hồ Sơ Cá Nhân</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Profile/profile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            /* Premium status styles */
            .premium-info {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 20px;
                border-radius: 12px;
                margin: 20px 0;
                text-align: center;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            }
            
            .premium-status {
                display: inline-block;
                padding: 8px 16px;
                border-radius: 20px;
                font-weight: bold;
                margin: 10px 0;
            }
            
            .premium-active {
                background: #28a745;
                color: white;
            }
            
            .premium-expired {
                background: #dc3545;
                color: white;
            }
            
            .premium-free {
                background: #6c757d;
                color: white;
            }
            
            .premium-details {
                margin-top: 15px;
                font-size: 14px;
                opacity: 0.9;
            }
            
            .upgrade-btn {
                display: inline-block;
                background: #ffc107;
                color: #000;
                padding: 10px 20px;
                text-decoration: none;
                border-radius: 25px;
                font-weight: bold;
                margin-top: 15px;
                transition: all 0.3s ease;
            }
            
            .upgrade-btn:hover {
                background: #e0a800;
                transform: translateY(-2px);
            }
            
            .premium-icon {
                font-size: 2em;
                margin-bottom: 10px;
            }
            
            .info-grid {
                grid-template-columns: 1fr;
                gap: 15px;
            }
            
            .info-section {
                background: white;
                padding: 20px;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                margin-bottom: 20px;
            }
            
            .section-title {
                font-size: 18px;
                font-weight: bold;
                color: #333;
                margin-bottom: 15px;
                border-bottom: 2px solid #f0f0f0;
                padding-bottom: 8px;
            }
            
            .info-row {
                display: flex;
                justify-content: space-between;
                padding: 12px 0;
                border-bottom: 1px solid #f5f5f5;
            }
            
            .info-row:last-child {
                border-bottom: none;
            }
            
            .info-label {
                font-weight: 600;
                color: #555;
                display: flex;
                align-items: center;
            }
            
            .info-label i {
                margin-right: 8px;
                color: #007bff;
                width: 20px;
            }
            
            .info-value {
                color: #333;
                text-align: right;
            }
            
            /* Back button styles */
            .back-btn {
                background: #007bff;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 5px;
                cursor: pointer;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                transition: all 0.3s ease;
            }
            
            .back-btn:hover {
                background: #0056b3;
                transform: translateY(-1px);
            }
            
            .back-btn i {
                font-size: 12px;
            }
        </style>
        <script>
            function goBack() {
                window.location.href = '${pageContext.request.contextPath}/HomeServlet';
            }
        </script>
    </head>
    <body>
        <!-- Back Button -->
        <div style="padding: 20px; background: #f8f9fa;">
            <button onclick="goBack()" class="back-btn">
                <i class="fas fa-arrow-left"></i>
                Quay lại
            </button>
        </div>
        
        <div class="container">
            <div class="sidebar">
                <div class="profile-pic">
                    <img src="<%= (avatarUrl != null && !avatarUrl.trim().isEmpty() ? avatarUrl : request.getContextPath() + "/" + defaultAvatar) %>" 
                         alt="avatar" 
                         style="width:100px;height:100px;border-radius:50%;object-fit:cover;">
                </div>
                <h3><%= user.getFullName()%></h3>
                <p class="title">ID: <%= user.getUserID()%></p>
                
                <!-- Premium Status Display -->
                <div class="premium-info">
                    <div class="premium-icon">
                        <% if (premiumInfo != null && daysRemaining > 0) { %>
                            <i class="fas fa-crown"></i>
                        <% } else { %>
                            <i class="fas fa-user"></i>
                        <% } %>
                    </div>
                    <div class="premium-status <%= premiumStatusClass %>">
                        <%= premiumStatus %>
                    </div>
                    <% if (premiumInfo != null && premiumInfo.getEndDate() != null) { %>
                        <div class="premium-details">
                            <i class="fas fa-calendar-alt"></i>
                            Hết hạn: <%= premiumEndDate %>
                        </div>
                    <% } %>
                    <% if (premiumInfo == null || daysRemaining <= 0) { %>
                        <a href="${pageContext.request.contextPath}/payment" class="upgrade-btn">
                            <i class="fas fa-star"></i> Nâng Cấp Premium
                        </a>
                    <% } %>
                </div>
                
                <ul class="menu">
                    <li><a href="${pageContext.request.contextPath}/HomeServlet"><i class="fas fa-home"></i> Trang Chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/editprofile"><i class="fas fa-edit"></i> Chỉnh Sửa</a></li>
                    <li><a href="${pageContext.request.contextPath}/LoginJSP/ChangePassword.jsp"><i class="fas fa-key"></i> Đổi Mật Khẩu</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a></li>
                </ul>
            </div>
            
            <div class="main">
                <div class="header">
                    <h2><i class="fas fa-user-circle"></i> Thông Tin Cá Nhân</h2>
                </div>

                <!-- Personal Information Section -->
                <div class="info-section">
                    <div class="section-title">
                        <i class="fas fa-address-card"></i> Thông Tin Cơ Bản
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-envelope"></i> Email:</div>
                        <div class="info-value"><%= user.getEmail() != null ? user.getEmail() : "Chưa cập nhật" %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-phone"></i> Số điện thoại:</div>
                        <div class="info-value"><%= user.getPhoneNumber() != null ? user.getPhoneNumber() : "Chưa cập nhật" %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-birthday-cake"></i> Ngày sinh:</div>
                        <div class="info-value">
                            <%= user.getBirthDate() != null ? new SimpleDateFormat("dd/MM/yyyy").format(user.getBirthDate()) : "Chưa cập nhật" %>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-venus-mars"></i> Giới tính:</div>
                        <div class="info-value"><%= user.getGender() != null ? user.getGender() : "Chưa cập nhật" %></div>
                    </div>
                </div>

                <!-- Study Information Section -->
                <div class="info-section">
                    <div class="section-title">
                        <i class="fas fa-graduation-cap"></i> Thông Tin Học Tập
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-language"></i> Trình độ tiếng Nhật:</div>
                        <div class="info-value">
                            <span style="background: #007bff; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;">
                                <%= user.getJapaneseLevel() != null ? user.getJapaneseLevel() : "Chưa xác định" %>
                            </span>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-user-tag"></i> Loại tài khoản:</div>
                        <div class="info-value">
                            <span style="background: <%= user.getRoleID() == 2 ? "#ffc107" : "#6c757d" %>; color: <%= user.getRoleID() == 2 ? "#000" : "#fff" %>; padding: 4px 8px; border-radius: 4px; font-size: 12px;">
                                <%= user.getRoleID() == 1 ? "Free" : user.getRoleID() == 2 ? "Premium" : user.getRoleID() == 3 ? "Teacher" : "Admin" %>
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Location Information Section -->
                <div class="info-section">
                    <div class="section-title">
                        <i class="fas fa-map-marker-alt"></i> Thông Tin Địa Chỉ
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-flag"></i> Quốc gia:</div>
                        <div class="info-value"><%= user.getCountry() != null ? user.getCountry() : "Chưa cập nhật" %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-home"></i> Địa chỉ:</div>
                        <div class="info-value"><%= user.getAddress() != null ? user.getAddress() : "Chưa cập nhật" %></div>
                    </div>
                </div>

                <!-- Account Information Section -->
                <div class="info-section">
                    <div class="section-title">
                        <i class="fas fa-cog"></i> Thông Tin Tài Khoản
                    </div>
                    <div class="info-row">
                        <div class="info-label"><i class="fas fa-calendar-plus"></i> Ngày tạo:</div>
                        <div class="info-value">
                            <%= user.getCreatedAt() != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(user.getCreatedAt()) : "Không xác định" %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
