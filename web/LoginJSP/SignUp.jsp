<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="form-box register">
    <!-- Form Đăng ký -->
    <form id="registerForm" action="${pageContext.request.contextPath}/login" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="signup">
        <h1>Đăng kí</h1>

        <div class="input-box">
            <input type="email" name="email" id="email" placeholder="Email"
                   value="<c:out value='${email}' default='' />" required>
            <i class='bx bxs-user'></i>
        </div>

        <div class="input-box">
            <input type="password" name="password" placeholder="Mật khẩu" required minlength="8">
            <i class='bx bxs-envelope'></i>
        </div>

        <div class="input-box">
            <input type="password" name="repass" placeholder="Xác nhận mật khẩu" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

 <!--Add chon role cho nay-->
        <div class="input-box">
            <label for="role" style="color: #e74c3c;">Bạn là:</label>
            <div class="custom-select-box">
                <select id="role" name="role" required>
                    <option value="">-- Chọn vai trò --</option>
                    <option value="student">Học sinh</option>
                    <option value="teacher">Giáo viên</option>
                </select>
            </div>
        </div>

        <!-- Upload file nếu là giáo viên -->
        <div class="input-box" id="certificateBox" style="display: none;">
            <label for="certificate" style="color: #e74c3c;">Chứng chỉ giảng dạy:</label>
            <input type="file" name="certificate" id="certificate" accept="application/pdf">
        </div>



        <button type="submit" class="btn" >Đăng kí</button>

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
    <form id="otpForm" action="${pageContext.request.contextPath}/verifyOtp" method="post" style="display: none;">
        <h1 style="margin-top: 5px;">Xác minh Email</h1>
        <p>Vui lòng nhập mã OTP để xác thực Email: <strong>${email}</strong></p>

        <div class="input-box" style="margin-top: 10px; margin-bottom: 20px">
            <input type="text" name="otp" id="otp" placeholder="Nhập mã OTP" maxlength="6" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <input type="hidden" name="email" id="otpEmail" value="${email}">
        <button type="submit" class="btn">Xác thực</button>

        <c:if test="${not empty message_otp}">
            <p class="error-message" style="color: red">${message_otp}</p>
        </c:if>

        <div id="otpMessage" style="color: green; margin-top: 5px;"></div>
        <button type="button" id="sendOtpBtn" class="btn" style="margin-top: 10px;">Gửi lại mã OTP</button>
    </form>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const sendBtn = document.getElementById("sendOtpBtn");
        const otpMessage = document.getElementById("otpMessage");
        const otpInput = document.getElementById("otp");
        const emailInput = document.getElementById("email");
        const otpForm = document.getElementById("otpForm");
        const registerForm = document.getElementById("registerForm");

        const email = emailInput ? emailInput.value : "";
        const otpEmail = document.getElementById("otpEmail") ? document.getElementById("otpEmail").value : "";

        // Kiểm tra email có hợp lệ hay không
        if (email && !email.includes('@')) {
            alert("Email không hợp lệ.");
            return;
        }

        // Chỉ gửi OTP sau khi đăng ký thành công và email hợp lệ
        if (registerForm && otpForm && email) {
            // Chuyển sang form OTP sau khi đăng ký thành công
            registerForm.style.display = "none";
            otpForm.style.display = "block";
            container.classList.add('active');
            container.classList.remove('active-change');

            // Gửi mã OTP tự động
            var xhr = new XMLHttpRequest();
            xhr.open("POST", "${pageContext.request.contextPath}/send-otp", true);
            xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhr.onload = function () {
                if (xhr.responseText.trim() === "ok") {
                    otpMessage.textContent = "Mã OTP đã được gửi thành công.";
                    otpMessage.style.color = "green";
                    startOtpTimeout(); // Bắt đầu đếm ngược sau khi mã OTP được gửi
                } else {
                    otpMessage.textContent = "Gửi OTP thất bại.";
                    otpMessage.style.color = "red";
                }
            };
            xhr.send("email=" + encodeURIComponent(email));
        }

        // Thêm sự kiện cho nút "Gửi lại OTP" sau 60 giây
        let otpTimeout;
        function startOtpTimeout() {
            let countdown = 60;
            sendBtn.disabled = true; // Vô hiệu hóa nút gửi lại OTP ngay sau khi gửi OTP
            otpMessage.textContent = "Vui lòng đợi " + countdown + " giây trước khi gửi lại mã OTP.";
            otpMessage.style.color = "orange";

            otpTimeout = setInterval(function () {
                countdown--;
                otpMessage.textContent = "Vui lòng đợi " + countdown + " giây trước khi gửi lại mã OTP.";

                if (countdown <= 0) {
                    clearInterval(otpTimeout);
                    sendBtn.disabled = false; // Kích hoạt lại nút gửi OTP sau 60 giây
                    otpMessage.textContent = "Bạn có thể gửi lại mã OTP.";
                    otpMessage.style.color = "green";
                }
            }, 1000);
        }

        // Gửi lại OTP
        sendBtn.addEventListener("click", function () {
            if (!email || email.length === 0) {
                otpMessage.textContent = "Email không hợp lệ. Vui lòng nhập email trước.";
                otpMessage.style.color = "red";
                return;
            }

            var xhr = new XMLHttpRequest();
            xhr.open("POST", "${pageContext.request.contextPath}/send-otp", true);
            xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhr.onload = function () {
                if (xhr.responseText.trim() === "ok") {
                    otpMessage.textContent = "Mã OTP đã được gửi thành công.";
                    otpMessage.style.color = "green";
                    startOtpTimeout(); // Bắt đầu lại đếm ngược sau khi gửi lại OTP
                } else {
                    otpMessage.textContent = "Gửi OTP thất bại.";
                    otpMessage.style.color = "red";
                }
            };
            xhr.send("email=" + encodeURIComponent(email));
        });

        // Khi mã OTP được nhập thành công
        const otpFormSubmit = otpForm.querySelector("form");
        otpFormSubmit.addEventListener("submit", function (event) {
            event.preventDefault(); // Ngừng gửi form thông thường

            const otpValue = otpInput.value.trim();
            if (otpValue.length !== 6) {
                otpMessage.textContent = "Mã OTP phải có 6 ký tự.";
                otpMessage.style.color = "red";
                return;
            }

            // Xử lý gửi OTP để xác minh
            var xhr = new XMLHttpRequest();
            xhr.open("POST", "${pageContext.request.contextPath}/verifyOtp", true);
            xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhr.onload = function () {
                if (xhr.responseText.trim() === "success") {
                    // OTP xác thực thành công
                    otpMessage.textContent = "Xác thực thành công!";
                    otpMessage.style.color = "green";
                    // Thực hiện đăng ký thành công ở đây
                    window.location.href = "${pageContext.request.contextPath}/login"; // Chuyển tới trang chào mừng hoặc trang cần thiết
                } else {
                    otpMessage.textContent = "Mã OTP không chính xác.";
                    otpMessage.style.color = "red";
                }
            };
            xhr.send("otp=" + encodeURIComponent(otpValue) + "&email=" + encodeURIComponent(otpEmail));
        });
    });
</script>
<!--role-->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const roleSelect = document.getElementById("role");
        const certificateBox = document.getElementById("certificateBox");

        roleSelect.addEventListener("change", function () {
            if (this.value === "teacher") {
                certificateBox.style.display = "block";
            } else {
                certificateBox.style.display = "none";
            }
        });
    });
</script>

