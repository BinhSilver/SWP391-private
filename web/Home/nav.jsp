<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<nav class="navbar navbar-expand-lg bg-light py-3">
    <div class="container-fluid">
        <div class="row align-items-center w-100">
            <div class="col-1 d-flex justify-content-evenly">
                <img src="${pageContext.request.contextPath}/image/logo.jpg" alt="Wasabii Logo" class="img-fluid" style="max-height: 50px;">
            </div>

            <div class="col-6">
                <div class="nav-links d-flex justify-content-evenly align-items-center h-100">
                    <a class="nav-link px-2" href="index.html">Trang Chủ</a>
                    <a class="nav-link px-2" href="#">Giới Thiệu</a>
                    <a class="nav-link px-2" href="courses.html">Khóa Học</a>
                    <a class="nav-link px-2" href="#">Liên Hệ</a>
                    <a class="nav-link px-2" href="#">Premium</a>
                </div>
            </div>

            <div class="col-3 d-flex justify-content-end">
                <div class="input-group" style="max-width: 100%;">
                    <span class="input-group-text"><i class="fas fa-search"></i></span>
                    <input type="search" class="form-control" placeholder="Tìm kiếm khóa học..." aria-label="Tìm kiếm khóa học">
                </div>
            </div>

            <div class="col-2 d-flex justify-content-end align-items-center gap-2">
                <a class="btn-wasabii" href="login">Đăng Nhập</a>
                <a class="btn-wasabii" href="${pageContext.request.contextPath}/LoginJSP/SignUp.jsp">Đăng Ký</a>
            </div>          
        </div>
    </div>
</nav>
