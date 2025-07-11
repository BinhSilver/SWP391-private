<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Nâng Cấp Premium</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/payment.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <style>
    .header-section {
      text-align: center;
      margin-bottom: 30px;
    }
    
    .back-button {
      display: inline-flex;
      align-items: center;
      background: #6c757d;
      color: white;
      padding: 10px 20px;
      text-decoration: none;
      border-radius: 25px;
      font-weight: 500;
      margin-bottom: 20px;
      transition: all 0.3s ease;
      position: absolute;
      top: 20px;
      left: 20px;
    }
    
    .back-button:hover {
      background: #5a6268;
      transform: translateY(-2px);
      color: white;
      text-decoration: none;
    }
    
    .back-button i {
      margin-right: 8px;
    }
    
    .premium-benefits {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 25px;
      border-radius: 15px;
      margin-bottom: 30px;
      text-align: center;
    }
    
    .benefits-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 15px;
      margin-top: 20px;
    }
    
    .benefit-item {
      display: flex;
      align-items: center;
      background: rgba(255,255,255,0.1);
      padding: 12px;
      border-radius: 8px;
    }
    
    .benefit-item i {
      margin-right: 10px;
      font-size: 1.2em;
      color: #ffc107;
    }
    
    .plan {
      position: relative;
      overflow: hidden;
    }
    
    .plan.popular::before {
      content: "Phổ Biến";
      position: absolute;
      top: 15px;
      right: -25px;
      background: #ff4757;
      color: white;
      padding: 5px 30px;
      font-size: 12px;
      font-weight: bold;
      transform: rotate(45deg);
      z-index: 1;
    }
    
    .container {
      position: relative;
    }
    
    .error-message {
      background: #f8d7da;
      color: #721c24;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
      border: 1px solid #f5c6cb;
    }
    
    .next-btn {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      color: white;
      padding: 15px 40px;
      font-size: 16px;
      font-weight: bold;
      border-radius: 25px;
      cursor: pointer;
      transition: all 0.3s ease;
      margin-top: 20px;
      width: 100%;
      max-width: 300px;
      display: block;
      margin-left: auto;
      margin-right: auto;
    }
    
    .next-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
    }
    
    .next-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
  </style>
</head>
<body>
  <div class="container">
    <!-- Back Button -->
    <a href="${pageContext.request.contextPath}/HomeServlet" class="back-button">
      <i class="fas fa-arrow-left"></i> Quay Lại Trang Chủ
    </a>

    <div class="header-section">
      <h2><i class="fas fa-crown"></i> Nâng Cấp Tài Khoản Premium</h2>
      <p style="color: #666; margin-top: 10px;">Mở khóa toàn bộ tính năng và nâng cao trải nghiệm học tập của bạn</p>
    </div>

    <!-- Premium Benefits -->
    <div class="premium-benefits">
      <h3><i class="fas fa-star"></i> Tại Sao Nên Chọn Premium?</h3>
      <div class="benefits-list">
        <div class="benefit-item">
          <i class="fas fa-infinity"></i>
          <span>Truy cập không giới hạn</span>
        </div>
        <div class="benefit-item">
          <i class="fas fa-headset"></i>
          <span>Hỗ trợ ưu tiên 24/7</span>
        </div>
        <div class="benefit-item">
          <i class="fas fa-download"></i>
          <span>Tải tài liệu offline</span>
        </div>
        <div class="benefit-item">
          <i class="fas fa-user-graduate"></i>
          <span>Khóa học chuyên sâu</span>
        </div>
        <div class="benefit-item">
          <i class="fas fa-certificate"></i>
          <span>Chứng chỉ hoàn thành</span>
        </div>
        <div class="benefit-item">
          <i class="fas fa-ad"></i>
          <span>Không quảng cáo</span>
        </div>
      </div>
    </div>

    <!-- Error Message -->
    <c:if test="${not empty errorMessage}">
        <div class="error-message">
          <i class="fas fa-exclamation-triangle"></i>
          ${errorMessage}
        </div>
    </c:if>

    <!-- Plan Selection Form -->
    <form id="planForm" action="${pageContext.request.contextPath}/CreatePayment" method="Post">
      <div class="plans">
        <c:forEach var="plan" items="${premiumPlans}" varStatus="status">
            <div class="plan ${status.index == 0 ? 'popular' : ''}" data-value="${plan.planID}">
              <h3>
                <i class="fas fa-gem"></i>
                ${plan.planName}
              </h3>
              <div class="price">
                <fmt:setLocale value="vi_VN"/>
                <fmt:formatNumber value="${plan.price}" type="currency" currencySymbol="₫"/> 
                <span style="font-size: 14px; color: #666;">/ ${plan.durationInMonths} tháng</span>
              </div>
              
              <!-- Price per month calculation -->
              <div style="font-size: 12px; color: #888; margin: 5px 0;">
                <fmt:formatNumber value="${plan.price / plan.durationInMonths}" pattern="#,###"/> ₫/tháng
              </div>
              
              <ul>
                <li><i class="fas fa-check"></i> ${plan.description}</li>
                <li><i class="fas fa-check"></i> Hỗ trợ ưu tiên</li>
                <li><i class="fas fa-check"></i> Không giới hạn truy cập</li>
                <li><i class="fas fa-check"></i> Tất cả tính năng Premium</li>
                <c:if test="${plan.durationInMonths >= 12}">
                  <li><i class="fas fa-star" style="color: #ffc107;"></i> <strong>Tiết kiệm ${Math.round((1 - (plan.price / plan.durationInMonths) / 25000) * 100)}%</strong></li>
                </c:if>
              </ul>
              <input type="radio" name="planId" value="${plan.planID}" hidden>
            </div>
        </c:forEach>
      </div>
      
      <button class="next-btn" type="submit" id="nextButton" disabled>
        <i class="fas fa-arrow-right"></i> Tiếp Theo - Chọn Phương Thức Thanh Toán
      </button>
    </form>

    <!-- Additional Information -->
    <div style="text-align: center; margin-top: 30px; color: #666; font-size: 14px;">
      <p><i class="fas fa-shield-alt"></i> Thanh toán an toàn và bảo mật 100%</p>
      <p><i class="fas fa-sync-alt"></i> Có thể hủy bất cứ lúc nào</p>
      <p><i class="fas fa-question-circle"></i> Cần hỗ trợ? Liên hệ: support@wasabii.com</p>
    </div>
  </div>

  <script>
    const plans = document.querySelectorAll('.plan');
    const radios = document.querySelectorAll('input[name="planId"]');
    const form = document.getElementById('planForm');
    const nextButton = document.getElementById('nextButton');

    plans.forEach(plan => {
      plan.addEventListener('click', () => {
        plans.forEach(p => p.classList.remove('selected'));
        plan.classList.add('selected');
        const input = plan.querySelector('input[type="radio"]');
        if (input) {
          input.checked = true;
          nextButton.disabled = false;
          nextButton.style.opacity = '1';
        }
      });
    });

    form.addEventListener('submit', function (e) {
      const checked = Array.from(radios).some(r => r.checked);
      if (!checked) {
        e.preventDefault();
        alert("Vui lòng chọn một gói để tiếp tục.");
      } else {
        // Add loading state
        nextButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
        nextButton.disabled = true;
      }
    });

    // Auto-select first plan if only one available
    if (plans.length === 1) {
      plans[0].click();
    }
  </script>
</body>
</html>
