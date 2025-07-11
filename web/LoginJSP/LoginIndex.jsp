<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Page</title>
        <link rel="stylesheet" href="<c:url value='/css/login.css' />">
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.2/gsap.min.js"></script>
        <script src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/16327/MorphSVGPlugin.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/2.1.3/TweenMax.min.js"></script>
        <link rel="stylesheet" href="<c:url value='/cssyeti/yeti.css' />">
        <script src="<c:url value='/cssyeti/yeti.js' />" defer></script>
    </head> 
    <body>
        <div class="container">
            <div class="toggle-box">
                <div class="toggle-panel toggle-left">
                    <%@include file="/LoginJSP/bear.jsp" %>
                    <p>Chưa có tài khoản?</p>
                    <button class="btn register-btn" id="registerBtn">Đăng kí</button>
                </div>
                <div class="toggle-panel toggle-right">
                    <h1>Xin chào</h1>
                    <p>Đã có sẵn tài khoản?</p>
                    <button class="btn login-btn" id="loginBtn">Đăng nhập</button>
                </div>
            </div>

            <c:import url="/LoginJSP/SignIn.jsp" />
            <c:import url="/LoginJSP/SignUp.jsp" />
            <c:import url="/LoginJSP/ForgotPassword.jsp" />
        </div>
        <script src="<c:url value='/js/login.js' />"></script>
        <script>
            window.onload = function () {
                // Kiểm tra nếu URL có chứa tham số 'signup'
                var urlParams = new URLSearchParams(window.location.search);
                if (urlParams.has('signup')) {               
                    container.classList.add('active');
                    container.classList.remove('active-change');
                }
            };
        </script>
    </body>

</html>