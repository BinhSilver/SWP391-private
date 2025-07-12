<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    
<div class="form-box change-password">

  <!-- Form gửi OTP -->
  <form id="sendOtpForm">
    <h1>Forgot Password</h1>
    <div class="input-box">
      <input type="email" id="forgotEmail" name="email" placeholder="Enter your email" required />
      <i class="bx bxs-envelope"></i>
    </div>
    <button type="submit" id="sendOtpBtn" class="btn">Send OTP</button>
    <p id="messageSendOtp"></p>
  </form>

  <!-- Form nhập OTP (ẩn lúc đầu) -->
  <form id="verifyOtpForm" style="display:none; margin-top: 20px;">
    <h1>Verify OTP</h1>
    <div class="input-box">
      <input type="text" id="forgotOtp" name="otp" placeholder="Enter OTP" maxlength="6" required />
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

  // Gửi OTP
  sendOtpForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const email = document.getElementById('forgotEmail').value.trim();
    if (!email) {
      messageSendOtp.style.color = 'red';
      messageSendOtp.textContent = 'Vui lòng nhập email.';
      return;
    }

    fetch(contextPath + '/send-forgot-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email: email })
    })
      .then(res => res.text())
      .then(response => {
        if (response.trim() === 'ok') {
          messageSendOtp.style.color = 'green';
          messageSendOtp.textContent = 'OTP đã được gửi đến email của bạn.';
          currentEmail = email;

          sendOtpForm.style.display = 'none';
          verifyOtpForm.style.display = 'block';
        } else {
          messageSendOtp.style.color = 'red';
          messageSendOtp.textContent = 'Gửi OTP thất bại. Vui lòng thử lại.';
        }
      })
      .catch(() => {
        messageSendOtp.style.color = 'red';
        messageSendOtp.textContent = 'Đã xảy ra lỗi khi gửi yêu cầu.';
      });
  });

  // Xác thực OTP
  verifyOtpForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const otp = document.getElementById('forgotOtp').value.trim();
    if (otp.length !== 6) {
      messageVerifyOtp.style.color = 'red';
      messageVerifyOtp.textContent = 'Mã OTP phải có đúng 6 ký tự.';
      return;
    }

    fetch(contextPath + '/verify-forgot-otp', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email: currentEmail, otp: otp })
    })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          messageVerifyOtp.style.color = 'green';
          messageVerifyOtp.textContent = 'Xác thực thành công. Vui lòng đặt lại mật khẩu.';

          verifyOtpForm.style.display = 'none';
          resetPasswordForm.style.display = 'block';
          document.getElementById('resetEmail').value = currentEmail;
        } else {
          messageVerifyOtp.style.color = 'red';
          messageVerifyOtp.textContent = data.message || 'Mã OTP không chính xác.';
        }
      })
      .catch(() => {
        messageVerifyOtp.style.color = 'red';
        messageVerifyOtp.textContent = 'Đã xảy ra lỗi khi xác thực OTP.';
      });
  });

  // Đặt lại mật khẩu
  resetPasswordForm.addEventListener('submit', function (e) {
    e.preventDefault();

    const newPass = document.getElementById('newPassword').value.trim();
    const confirmPass = document.getElementById('confirmPassword').value.trim();
    const email = document.getElementById('resetEmail').value;

    if (newPass.length < 6) {
      messageResetPass.style.color = 'red';
      messageResetPass.textContent = 'Mật khẩu phải từ 6 ký tự trở lên.';
      return;
    }

    if (newPass !== confirmPass) {
      messageResetPass.style.color = 'red';
      messageResetPass.textContent = 'Mật khẩu không khớp.';
      return;
    }

    fetch(contextPath + '/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ email: email, newPassword: newPass })
    })
      .then(res => res.text())
      .then(response => {
        if (response.trim() === 'ok') {
          messageResetPass.style.color = 'green';
          messageResetPass.textContent = 'Đặt lại mật khẩu thành công. Vui lòng đăng nhập.';
        } else {
          messageResetPass.style.color = 'red';
          messageResetPass.textContent = 'Đặt lại mật khẩu thất bại.';
        }
      })
      .catch(() => {
        messageResetPass.style.color = 'red';
        messageResetPass.textContent = 'Lỗi khi gửi yêu cầu.';
      });
  });
</script>
