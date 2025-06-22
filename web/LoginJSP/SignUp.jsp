<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="form-box register">
    <!-- Form Đăng ký -->
    <form id="registerForm" action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="action" value="signup">
        <h1>Đăng kí</h1>

        <div class="input-box">
            <input type="email" name="email" placeholder="Email"
                   value="<c:out value='${email}' default='' />" required>
            <i class='bx bxs-user'></i>
        </div>

        <div class="input-box">
            <input type="password" name="password" placeholder="Password" required minlength="8">
            <i class='bx bxs-envelope'></i>
        </div>

        <div class="input-box">
            <input type="password" name="repass" placeholder="Confirm Password" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <div class="input-box">
            <label for="gender" style="margin-bottom: 4px; display: block; color: #333;">Giới tính:</label>
            <select name="gender" id="gender" required style="width: 100%; padding: 8px; border-radius: 6px; border: 1px solid #ccc;">
                <option value="Nam">Nam</option>
                <option value="Nữ">Nữ</option>
                <option value="Khác">Khác</option>
            </select>
        </div>

        <button type="submit" class="btn">Đăng kí</button>

        <div class="social-icons" style="margin-top: 10px">
            <a href="#"><i class='bx bxl-google'></i></a>
            <a href="#"><i class='bx bxl-facebook'></i></a>
            <a href="#"><i class='bx bxl-github'></i></a>
            <a href="#"><i class='bx bxl-linkedin'></i></a>
        </div>

        <c:if test="${not empty message_signup}">
            <p class="error-message" style="color: red">${message_signup}</p>
        </c:if>
    </form>

    <!-- Form OTP -->
    <form id="otpForm" action="${pageContext.request.contextPath}/verifyOtp " method="post" style="display: none;">
        <h1 style="margin-top: 5px;">Xác minh Email</h1>
        <p>Vui lòng nhấn Gửi mã OTP để xác thực Email: <strong>${email}</strong></p>

        <div class="input-box" style="margin-top: 10px; margin-bottom: 20px">
            <input type="text" name="otp" id="otp" placeholder="Nhập mã OTP" maxlength="6" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <input type="hidden" name="email" value="${email}">
        <button type="submit" class="btn">Xác thực</button>

        <c:if test="${not empty message_otp}">
            <p class="error-message" style="color: red">${message_otp}</p>
        </c:if>

        <div id="otpMessage" style="color: green; margin-top: 5px;"></div>
        <button type="button" id="sendOtpBtn" class="btn" style="margin-top: 10px;">Gửi mã OTP</button>
    </form>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const sendBtn = document.getElementById("sendOtpBtn");
        const otpMessage = document.getElementById("otpMessage");

        if (sendBtn) {
            sendBtn.addEventListener("click", function () {
                const email = "${email}";
                if (!email) {
                    alert("Không có email để gửi OTP.");
                    return;
                }

                var xhr = new XMLHttpRequest();
                xhr.open("POST", "${pageContext.request.contextPath}/send-otp",     true);
                xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                xhr.onload = function () {
                    if (xhr.responseText.trim() === "ok") {
                        otpMessage.textContent = "Mã OTP đã được gửi thành công.";
                        otpMessage.style.color = "green";
                    } else {
                        otpMessage.textContent = "Gửi OTP thất bại.";
                        otpMessage.style.color = "red";
                    }
                };
                xhr.send("email=" + encodeURIComponent(email));
            });
        }

        const showOtpForm = "${showOtpForm}";
        const otpForm = document.getElementById("otpForm");
        const registerForm = document.getElementById("registerForm");

        if (showOtpForm === "true") {
            if (otpForm) otpForm.style.display = "block";
            if (registerForm) registerForm.style.display = "none";
        }

        var showRegisterForm = "${registerActive}";
        if (showRegisterForm.trim() === "active") {
            document.querySelector(".container")?.classList.add("active");
        }
    });
</script>