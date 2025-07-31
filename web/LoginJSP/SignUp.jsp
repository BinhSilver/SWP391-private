<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="form-box register">
    <!-- Form Đăng ký -->
    <form id="registerForm" action="${pageContext.request.contextPath}/login" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="signup">
        <input type="hidden" id="registerActive" name="registerActive" value="${param.registerActive}" />
        <h1>Đăng kí</h1>

        <div class="input-box">
            <input type="text" name="fullName" id="fullName" placeholder="User name"
                   value="<c:out value='${fullName}' default='' />" required>
            <i class='bx bxs-user'></i>
        </div>

        <div class="input-box">
            <input type="email" name="email" id="registerEmail" placeholder="Email"
                   value="<c:out value='${not empty registerActive ? email : ""}' default='' />" required>
            <i class='bx bxs-envelope'></i>
        </div>

        <div class="input-box">
            <input type="password" name="password" placeholder="Mật khẩu" required minlength="8">
            <i class='bx bxs-envelope'></i>
        </div>

        <div class="input-box">
            <input type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <div class="input-box">
            <label for="gender" style="color: #e74c3c;">Giới tính:</label>
            <div class="custom-select-box">
                <select name="gender" id="gender">
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                    <option value="Khác">Khác</option>
                </select>
            </div>
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
            <label for="certificate" style="color: #e74c3c;">
                <i class="fas fa-certificate"></i> Chứng chỉ giảng dạy: <span style="color: #e74c3c;">*</span>
            </label>
            <input type="file" name="certificate" id="certificate" accept="application/pdf" required>
            <small style="color: #666; font-size: 12px; display: block; margin-top: 5px;">
                <i class="fas fa-info-circle"></i> 
                Yêu cầu: File PDF, tối đa 10MB, chứng chỉ phải còn hiệu lực
            </small>
        </div>



        <button type="submit" class="btn" >Đăng kí</button>

        <div class="social-icons" style="margin-top: 10px">
            <a href="#"><i class='bx bxl-google'></i></a>
            <a href="#"><i class='bx bxl-facebook'></i></a>
            <a href="#"><i class='bx bxl-github'></i></a>
            <a href="#"><i class='bx bxl-linkedin'></i></a>
        </div>

        <c:if test="${not empty message_signup}">
            <div class="alert alert-danger" style="margin-top: 15px; padding: 10px; border-radius: 5px; background-color: #f8d7da; border: 1px solid #f5c6cb; color: #721c24;">
                <i class="fas fa-exclamation-triangle"></i>
                <strong>Lỗi:</strong> ${message_signup}
            </div>
        </c:if>
    </form>

    <!-- Form OTP -->
    <form id="otpForm" action="${pageContext.request.contextPath}/verifyOtp" method="post" style="display: none;">
        <input type="hidden" name="email" id="otpEmail" value="${sessionScope.pending_email != null ? sessionScope.pending_email : param.email}" />
        <input type="hidden" name="password" value="${password}">
        <input type="hidden" name="fullName" value="${fullName}">
        <input type="hidden" name="gender" value="${gender}">
        <input type="hidden" name="role" value="${role}">
        <h1 style="margin-top: 5px;">Xác minh Email</h1>
        <p>Vui lòng nhập mã OTP để xác thực Email: <strong>${email}</strong></p>

        <div class="input-box" style="margin-top: 10px; margin-bottom: 20px">
            <input type="text" name="otp" id="otp" placeholder="Nhập mã OTP" maxlength="6" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <input type="hidden" name="email" id="otpEmail" value="${email}">
        <button type="submit" class="btn" id="verifyBtn">Xác thực</button>

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
        const veriBtn = document.getElementById("verifyBtn");
        const otpMessage = document.getElementById("otpMessage");
        const otpInput = document.getElementById("otp");
        const emailInput = document.getElementById("registerEmail");
        const otpForm = document.getElementById("otpForm");
        const registerForm = document.getElementById("registerForm");
        const registerActive = document.getElementById("registerActive") ? document.getElementById("registerActive").value : "";
        const otpEmail = document.getElementById("otpEmail") ? document.getElementById("otpEmail").value : "";
        const email = emailInput ? emailInput.value : otpEmail;

        // Chỉ chuyển sang OTP khi thực sự submit form đăng ký
        if (registerActive === "true" && registerForm && otpForm && email && email.trim() !== "") {
            registerForm.style.display = "none";
            otpForm.style.display = "block";
            const container = document.querySelector(".container");
            if (container) {
                container.classList.add('active');
                container.classList.remove('active-change');
            }
        }

        let otpTimeout;
        function startOtpTimeout() {
            let countdown = 60;
            sendBtn.disabled = true;
            otpMessage.textContent = "Vui lòng đợi " + countdown + " giây trước khi gửi lại mã OTP.";
            otpMessage.style.color = "orange";

            otpTimeout = setInterval(function () {
                countdown--;
                otpMessage.textContent = "Vui lòng đợi " + countdown + " giây trước khi gửi lại mã OTP.";
                if (countdown <= 0) {
                    clearInterval(otpTimeout);
                    sendBtn.disabled = false;
                    otpMessage.textContent = "Bạn có thể gửi lại mã OTP.";
                    otpMessage.style.color = "green";
                }
            }, 1000);
        }

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
                    startOtpTimeout();
                } else {
                    otpMessage.textContent = "Gửi OTP thất bại.";
                    otpMessage.style.color = "red";
                }
            };
            xhr.send("email=" + encodeURIComponent(email));
        });

        // Xử lý khi submit OTP
        otpForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const otpValue = otpInput.value.trim();
            if (otpValue.length !== 6) {
                otpMessage.textContent = "Mã OTP phải có 6 ký tự.";
                otpMessage.style.color = "red";
                return;
            }

            var xhr = new XMLHttpRequest();
            xhr.open("POST", "${pageContext.request.contextPath}/verifyOtp", true);
            xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xhr.onload = function () {
                const responseJson = JSON.parse(xhr.responseText);
                if (responseJson.success) {
                    otpMessage.textContent = "✅ Xác thực thành công, tài khoản đã được tạo!";
                    otpMessage.style.color = "green";

                    if (otpTimeout) {
                        clearInterval(otpTimeout);
                    }

                    sendBtn.style.display = "none";
                    veriBtn.style.display = "none";
                    otpMessage.style.color = "green";

                    // ✅ Ẩn form OTP hoặc chuyển trang sau vài giây (tùy bạn)
                    setTimeout(function () {                 
                        otpMessage.textContent = "✅ Xác thực thành công, tài khoản đã được tạo!";
                        
                        window.location.href = "${pageContext.request.contextPath}/index.jsp";
                    }, 3000);
                } else {
                    otpMessage.textContent = responseJson.message || "Mã OTP không chính xác.";
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
        const certificateInput = document.getElementById("certificate");
        const registerForm = document.getElementById("registerForm");
        
        roleSelect.addEventListener("change", function () {
            if (this.value === "teacher") {
                certificateBox.style.display = "block";
                certificateInput.required = true;
            } else {
                certificateBox.style.display = "none";
                certificateInput.required = false;
            }
        });
        
        // Validation cho file certificate
        certificateInput.addEventListener("change", function() {
            const file = this.files[0];
            if (file) {
                // Kiểm tra kích thước file (10MB)
                const maxSize = 10 * 1024 * 1024; // 10MB
                if (file.size > maxSize) {
                    alert("❌ File quá lớn! Kích thước tối đa là 10MB. File hiện tại: " + 
                        (file.size / (1024 * 1024)).toFixed(2) + "MB");
                    this.value = "";
                    return;
                }
                
                // Kiểm tra định dạng file
                if (!file.name.toLowerCase().endsWith('.pdf')) {
                    alert("❌ Chỉ chấp nhận file PDF! File hiện tại: " + file.name);
                    this.value = "";
                    return;
                }
                
                // Kiểm tra content type
                if (file.type !== 'application/pdf') {
                    alert("❌ File không phải định dạng PDF hợp lệ!");
                    this.value = "";
                    return;
                }
                
                console.log("✅ File hợp lệ:", file.name, "Size:", (file.size / 1024).toFixed(2) + "KB");
            }
        });
        
        // Validation khi submit form
        registerForm.addEventListener("submit", function(e) {
            if (roleSelect.value === "teacher") {
                const file = certificateInput.files[0];
                if (!file) {
                    e.preventDefault();
                    alert("❌ Vui lòng chọn file chứng chỉ để đăng ký làm giáo viên!");
                    return false;
                }
            }
        });
    });
</script>