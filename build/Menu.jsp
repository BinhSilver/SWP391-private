<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!--begin of menu-->
<nav class="navbar navbar-expand-md navbar-dark bg-dark" style="position: fixed; top: 0; width:100%;  z-index: 100000;">
    <div class="container">
        <a class="navbar-brand" href="home">Wasabii</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse justify-content-end" id="navbarsExampleDefault">
            <ul class="navbar-nav m-auto">
                <li class="nav-item">
                    <a class="nav-link" href="home">Home</a>
                </li>

                <!-- Nếu CHƯA đăng nhập -->
                <c:if test="${sessionScope.authUser == null}">
                    <li class="nav-item">
                        <a class="nav-link" href="LoginJSP/LoginIndex.jsp">Login</a>
                    </li>
                </c:if>

                <!-- Nếu ĐÃ đăng nhập -->
                <c:if test="${sessionScope.authUser != null}">
                    <li class="nav-item">
                        <a class="nav-link" href="logout">Logout</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="Profile/profile-view.jsp">My Profile</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="VideoCall/call.jsp">Video Call</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="PaymentJSP/Payment.jsp">Upgrade Premium</a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>
<!--end of menu-->
