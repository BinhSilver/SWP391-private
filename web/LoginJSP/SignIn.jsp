<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="form-box login">
    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="hidden" name="action" value="signin">
        <h1>Login</h1>
        <div class="input-box">
            <input type="email"id="email" name="email" placeholder="Email" value="${not empty email ? email : ''}"
                   required>
            <i class='bx bxs-user'></i>
        </div>
        <div class="input-box">
            <input type="password" id="password" name="password" placeholder="Password"
                   value="${not empty password ? password : ''}" required>
            <i class='bx bxs-lock-alt'></i>
        </div>

        <div class="box-box">
            <div class="checkbox-group">
                <label for="rememberMe">Remember me</label>
                <input type="checkbox" id="rememberMe" name="rememberMe" ${"on".equals(rememberMe) ? "checked"
                                                                            : "" } />
            </div>
            <div class="forgot-link">
                <a href="#">Forgot Password?</a>
            </div>
        </div>

        <button type="submit" class="btn">Login</button>
        <p>or login with social platforms</p>
        <div class="social-icons">
            <a href="${pageContext.request.contextPath}/login-google"><i class='bx bxl-google'></i></a>
            <a href="#"><i class='bx bxl-facebook'></i></a>
            <a href="#"><i class='bx bxl-github'></i></a>
            <a href="#"><i class='bx bxl-linkedin'></i></a>
        </div>
        <c:if test="${not empty message}">
            <p class="error-message" style="color: red">${message}</p>
        </c:if>
    </form>

</div>