<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    .navbar {
        position: relative;
    }
    .nav-links .nav-link {
        font-size: 1rem;
        color: #333;
        transition: color 0.3s;
    }
    .nav-links .nav-link:hover {
        color: #28a745;
    }
    .input-group {
        max-width: 250px;
    }

    .search-container {
        position: relative;
    }
    #searchResults {
        position: absolute;
        top: 100%;
        right: 0;
        width: 100%; /* Khớp với .input-group */
        max-height: 400px;
        overflow-y: auto;
        background: white;
        border: 1px solid #ddd;
        border-radius: 0.25rem;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        z-index: 1000;
        display: none;
        padding: 0;
    }
    #searchResults.show {
        display: block;
    }
    #searchResults .list-group-item {
        border: none;
        padding: 0.75rem;
        display: block;
    }
    #searchResults .list-group-item h5 {
        margin: 0;
        font-size: 1rem;
        color: #333;
        display: block;
    }
    #searchResults .list-group-item p {
        margin: 0.25rem 0 0;
        font-size: 0.85rem;
        color: #666;
        display: block;
    }
    #searchResults .btn-wasabii {
        font-size: 0.8rem;
        padding: 0.25rem 0.5rem;
        margin-top: 0.5rem;
    }
    .dropdown-menu {
        min-width: 120px;
    }
    @media (max-width: 992px) {
        .input-group {
            max-width: 200px;
        }
        #searchResults {
            width: 100%;
        }
    }
</style>

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

                    <a href="${pageContext.request.contextPath}/HomeServlet" class="flex items-center space-x-2 hover:text-blue-500"><span>👤</span><span>Trang Chủ</span></a>
                    <a href="${pageContext.request.contextPath}/userManagement" class="flex items-center space-x-2 hover:text-blue-500"><span>👤</span><span>Người dùng</span></a>
                    <a href="${pageContext.request.contextPath}/teacherApproval" class="flex items-center space-x-2 hover:text-blue-500"><span>👨‍🏫</span><span>Xác nhận giáo viên</span></a>
                    <a href="${pageContext.request.contextPath}/courseManagement" class="flex items-center space-x-2 hover:text-blue-500"><span>📚</span><span>Khóa học</span></a>
                    <a href="${pageContext.request.contextPath}/admin/premium-plans" class="flex items-center space-x-2 hover:text-blue-500"><span>💰</span><span>Doanh thu</span></a>
                    <a href="${pageContext.request.contextPath}/BulkEmailAdmin.jsp" class="flex items-center space-x-2 hover:text-blue-500"><span>📄</span><span>Gửi Mail</span></a>

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
                        <div class="dropdown">
                            <a class="btn dropdown-toggle d-flex align-items-center" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                <ion-icon name="person-circle-outline" style="font-size: 24px;"></ion-icon>
                                <span class="ms-2">${authUser.fullName}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                <li>
                                    <a class="dropdown-item" href="<c:url value='profile'/>">Profile</a>
                                </li>
                                <c:if test="${authUser.roleID == 3}">
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='teacher_dashboard'/>">Dashboard</a>
                                    </li>
                                </c:if>
                                <c:if test="${authUser.roleID == 4}">
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='statis.jsp'/>">Admin</a>
                                    </li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item text-danger" href="<c:url value='/logout'/>">Đăng Xuất</a>
                                </li>
                            </ul>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
