<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Nâng Cấp Premium</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/payment.css">
</head>
<body>
  <div class="container">
    <h2>Nâng Cấp Tài Khoản Premium</h2>

    <c:if test="${not empty errorMessage}">
        <p style="color: red;">${errorMessage}</p>
    </c:if>

    <form id="planForm" action="${pageContext.request.contextPath}/CreatePayment" method="Post">
      <div class="plans">
        <c:forEach var="plan" items="${premiumPlans}">
            <div class="plan" data-value="${plan.planID}">
              <h3>${plan.planName}</h3>
              <div class="price">
                  <fmt:setLocale value="vi_VN"/>
                  <fmt:formatNumber value="${plan.price}" type="currency" currencySymbol="₫"/> / ${plan.durationInMonths} tháng
              </div>
              <ul>
                <li>${plan.description}</li>
                <li>Hỗ trợ ưu tiên</li>
                <li>Không giới hạn</li>
              </ul>
              <input type="radio" name="planId" value="${plan.planID}" hidden>
            </div>
        </c:forEach>
      </div>
      <button class="next-btn" type="submit">Tiếp Theo</button>
    </form>
  </div>

  <script>
    const plans = document.querySelectorAll('.plan');
    const radios = document.querySelectorAll('input[name="planId"]');
    const form = document.getElementById('planForm');

    plans.forEach(plan => {
      plan.addEventListener('click', () => {
        plans.forEach(p => p.classList.remove('selected'));
        plan.classList.add('selected');
        const input = plan.querySelector('input[type="radio"]');
        if (input) input.checked = true;
      });
    });

    form.addEventListener('submit', function (e) {
      const checked = Array.from(radios).some(r => r.checked);
      if (!checked) {
        e.preventDefault();
        alert("Vui lòng chọn một gói để tiếp tục.");
      }
    });
  </script>
</body>
</html>
