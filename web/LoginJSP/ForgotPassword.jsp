<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    
<div class="form-box change-password">

  <!-- Form gửi OTP -->
  <form id="sendOtpForm">
    <h1>Forgot Password</h1>
    <div class="input-box">
      <input type="email" id="email" name="email" placeholder="Enter your email" required />
      <i class="bx bxs-envelope"></i>
    </div>
    <button type="submit" id="sendOtpBtn" class="btn">Send OTP</button>
    <p id="messageSendOtp"></p>
  </form>

  <!-- Form nhập OTP (ẩn lúc đầu) -->
  <form id="verifyOtpForm" style="display:none; margin-top: 20px;">
    <h1>Verify OTP</h1>
    <div class="input-box">
      <input type="text" id="otp" name="otp" placeholder="Enter OTP" maxlength="6" required />
      <i class="bx bxs-key"></i>
    </div>
    <button type="submit" id="verifyOtpBtn" class="btn">Verify OTP</button>
    <p id="messageVerifyOtp"></p>
  </form>
  
  <!-- Form đặt lại mật khẩu -->
  <form id="resetPasswordForm" style="display:none;">
    <h1>Reset Password</h1>
    <input type="hidden" id="resetEmail" name="email" />

    <div class="input-box">
      <input type="password" id="newPassword" name="newPassword" placeholder="New Password" required />
      <span class="input-icon"><i class="bx bxs-lock-alt"></i></span>
    </div>

    <div class="input-box">
      <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm Password" required minlength="8"/>
      <i class="bx bxs-lock-alt"></i>
    </div>
    <button type="submit" class="btn">Reset Password</button>
    <p id="messageResetPass"></p>
  </form>

</div>
<script>
  const contextPath = '${pageContext.request.contextPath}';
  let currentEmail = '';

  const sendOtpForm = document.getElementById('sendOtpForm');
  const verifyOtpForm = document.getElementById('verifyOtpForm');
  const resetPasswordForm = document.getElementById('resetPasswordForm');
  const messageSendOtp = document.getElementById('messageSendOtp');
  const messageVerifyOtp = document.getElementById('messageVerifyOtp');
  const messageResetPass = document.getElementById('messageResetPass');


// Xử lý gửi OTP
  sendOtpForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const email = sendOtpForm.email.value.trim();
    if (!email) {
      messageSendOtp.style.color = 'red';
      messageSendOtp.textContent = 'Vui lòng nhập email.';
      return;
    }

    fetch(contextPath + '/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        action: 'forgot_pass',
        email: email
      }),
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        messageVerifyOtp.style.color = 'green';
        messageVerifyOtp.textContent = 'Mã OTP đã được gửi vào email của bạn.';
        currentEmail = email;

        // Ẩn form gửi OTP, hiện form nhập OTP
        sendOtpForm.style.display = 'none';
        verifyOtpForm.style.display = 'block';
      } else {
        messageSendOtp.style.color = 'red';
        messageSendOtp.textContent = data.message || 'Gửi mã OTP thất bại.';
      }
    })
    .catch(() => {
      messageSendOtp.style.color = 'red';
      messageSendOtp.textContent = 'Lỗi khi gửi yêu cầu.';
    });
  });

// Xử lý xác thực OTP
  verifyOtpForm.addEventListener('submit', function(e) {
    e.preventDefault();

    const otp = verifyOtpForm.otp.value.trim();
    if (!otp) {
      messageVerifyOtp.style.color = 'red';
      messageVerifyOtp.textContent = 'Vui lòng nhập mã OTP.';
      return;
    }

    if (!currentEmail) {
      messageVerifyOtp.style.color = 'red';
      messageVerifyOtp.textContent = 'Vui lòng gửi mã OTP trước.';
      return;
    }

    fetch(contextPath + '/verifyOtp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        email: currentEmail,
        otp: otp
      }),
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        messageResetPass.style.color = 'green';
        messageResetPass.textContent = 'Xác thực OTP thành công. Vui lòng đặt lại mật khẩu.';
        verifyOtpForm.style.display = 'none';
        resetPasswordForm.style.display = 'block';
        document.getElementById('resetEmail').value = currentEmail;
      } else {
        messageVerifyOtp.style.color = 'red';
        messageVerifyOtp.textContent = data.message || 'Xác thực OTP thất bại.';
      }
    })
    .catch(() => {
      messageVerifyOtp.style.color = 'red';
      messageVerifyOtp.textContent = 'Lỗi khi gửi yêu cầu.';
    });
  });
  
// Đặt lại mật khẩu
  resetPasswordForm.addEventListener('submit', function (e) {
    e.preventDefault();
    const newPass = document.getElementById('newPassword').value.trim();
    const confirmPass = document.getElementById('confirmPassword').value.trim();
    const email = document.getElementById('resetEmail').value;

    if (newPass !== confirmPass) {
      messageResetPass.style.color = 'red';
      messageResetPass.textContent = 'Mật khẩu không khớp.';
      return;
    }

    fetch(contextPath + '/login', {
      method: 'POST',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      body: new URLSearchParams({
        action: 'reset_pass',
        email: email,
        newPassword: newPass
      })
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        messageResetPass.style.color = 'green';
        messageResetPass.textContent = 'Đặt lại mật khẩu thành công. Vui lòng đăng nhập.';
      } else {
        messageResetPass.style.color = 'red';
        messageResetPass.textContent = data.message || 'Có lỗi xảy ra.';
      }
    });
  });
</script>

