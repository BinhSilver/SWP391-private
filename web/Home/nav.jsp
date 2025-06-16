<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar navbar-expand-lg bg-light py-3">
    <div class="container-fluid">
        <div class="row align-items-center w-100">
            <!-- Logo -->
            <div class="col-1 d-flex justify-content-evenly">
                <img src="<c:url value='/image/logo.jpg'/>" alt="Wasabii Logo" class="img-fluid" style="max-height: 50px;">
            </div>

            <!-- Navigation Links -->
            <div class="col-6">
                <div class="nav-links d-flex justify-content-evenly align-items-center h-100">
                    <a class="nav-link px-2" href="<c:url value='/index.jsp'/>">Trang Chủ</a>
                    <a class="nav-link px-2" href="#">Giới Thiệu</a>
                    <c:choose>
                        <c:when test="${authUser.roleID == 1 || authUser.roleID == 2}">
                            <a class="nav-link px-2" href="CoursesServlet">Khóa Học</a>
                        </c:when>
                        <c:when test="${authUser.roleID == 3 || authUser.roleID == 4}">
                            <a class="nav-link px-2" href="teacher_dashboard">Khóa Học</a>
                        </c:when>
                    </c:choose>

                    <a class="nav-link px-2" href="#">Liên Hệ</a>
                    <a class="nav-link px-2" href="<c:url value='/PaymentJSP/Payment.jsp'/>">Premium</a>
                    <a class="nav-link px-2" href="#">FlashCard</a>

                </div>
            </div>

            <!-- Search -->
            <div class="col-3 d-flex justify-content-end">
                <div class="input-group" style="max-width: 100%;">
                    <span class="input-group-text"><i class="fas fa-search"></i></span>
                    <input type="search" class="form-control" placeholder="Tìm kiếm khóa học..." aria-label="Tìm kiếm khóa học">
                </div>
            </div>

            <!-- Auth Links -->
            <div class="col-2 d-flex justify-content-end align-items-center gap-2">
                <c:choose>
                    <c:when test="${empty authUser}">
                        <a href="<c:url value='login' />" class="btn-wasabii">Đăng Nhập</a>
                        <a href="<c:url value='register' />" class="btn-wasabii">Đăng Ký</a>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex align-items-center gap-2">
                            <ion-icon name="person-outline"></ion-icon>
                            <span>${authUser.fullName}</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>


        </div>
    </div>
</nav>
