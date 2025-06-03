<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Nâng Cấp Premium</title>
  <link rel="stylesheet" href="../css/payment.css">
</head>
<body>
  <div class="container">
    <h2>Nâng Cấp Tài Khoản Premium</h2>

    <form id="planForm" action="/CreatePayment" method="post">
      <div class="plans">
        <!-- Gói tháng -->
        <div class="plan" data-value="month">
          <h3>Gói Hàng Tháng</h3>
          <div class="price">25.000₫ /tháng</div>
          <ul>
            <li>Truy cập đầy đủ tính năng</li>
            <li>Hỗ trợ ưu tiên</li>
            <li>Không giới hạn</li>
          </ul>
          <input type="radio" name="plan" value="month" hidden>
        </div>

        <!-- Gói năm -->
        <div class="plan" data-value="year">
          <h3>Gói Hàng Năm <span class="badge">Phổ biến</span></h3>
          <div class="price">
            <span class="original-price">300.000₫</span>
            <span style="color:#10b981;">250.000₫</span> /năm
          </div>
          <ul>
            <li>Tiết kiệm 2 tháng</li>
            <li>Hỗ trợ ưu tiên</li>
            <li>Không giới hạn</li>
          </ul>
          <input type="radio" name="plan" value="year" hidden>
        </div>
      </div>
      <button class="next-btn" type="submit">Tiếp Theo</button>
    </form>
  </div>

  <script>
    const plans = document.querySelectorAll('.plan');
    const radios = document.querySelectorAll('input[name="plan"]');
    const form = document.getElementById('planForm');

    plans.forEach(plan => {
      plan.addEventListener('click', () => {
        // Bỏ chọn các ô khác
        plans.forEach(p => p.classList.remove('selected'));
        // Đánh dấu được chọn
        plan.classList.add('selected');
        // Gán value tương ứng
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
