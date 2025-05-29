<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="form-box register">
    <form action="${pageContext.request.contextPath}/login" method="post" id="registerForm">
        <input type="hidden" name="action" value="signup">
        <h1 style="margin-top: 5px;">Registration</h1>

        <div class="input-box" style="margin-top: 10px; margin-bottom: 20px">
            <input type="email" name="email" id="email" placeholder="Email" 
                   value="<c:out value='${email}' default='' />" required>
            <i class='bx bxs-user'></i>
        </div>
            
        <button type="button" id="sendOtpBtn" class="btn" style="margin-top: -20px; margin-bottom: -20px;">Gửi mã OTP</button>
                
        <div class="input-box">
            <input type="text" name="otp" id="otp" placeholder="Nhập mã OTP" maxlength="6" required>
            <i class='bx bxs-lock-alt'></i>
        </div>
            
        <div class="input-box">
            <input type="password" name="password" placeholder="Password" required minlength="8">
            <i class='bx bxs-envelope'></i>
        </div>

        <div class="input-box">
            <input type="password" name="repass" placeholder="Confirm Password" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <button type="submit" class="btn">Register</button>
        
        <div class="social-icons" style="margin-top: 10px">
            <a href="#"><i class='bx bxl-google'></i></a>
            <a href="#"><i class='bx bxl-facebook'></i></a>
            <a href="#"><i class='bx bxl-github'></i></a>
            <a href="#"><i class='bx bxl-linkedin'></i></a>
        </div>

        <c:if test="${not empty message_signup}">
            <p class="error-message" style="color: red">${message_signup}</p>
        </c:if>
        <div id="otpMessage" style="color: green; margin-top: 5px;"></div>
    </form>
</div>

<script>
document.getElementById("sendOtpBtn").addEventListener("click", function() {
    var email = document.getElementById("email").value.trim();
    if (!email) {
        alert("Vui lòng nhập email trước khi gửi mã OTP.");
        return;
    }
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "${pageContext.request.contextPath}/send-otp", true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhr.onload = function() {
        if (xhr.responseText === "ok") {
            document.getElementById("otpMessage").textContent = "Mã OTP đã được gửi tới email của bạn.";
        } else {
            document.getElementById("otpMessage").textContent = "Gửi mã OTP thất bại. Vui lòng thử lại.";
        }
    };
    xhr.send("email=" + encodeURIComponent(email));
});

    document.addEventListener("DOMContentLoaded", function () {
        var showRegisterForm = "${registerActive}";
        if (showRegisterForm.trim() === "active") {
            document.querySelector(".container").classList.add("active");
        }
    });
</script>
