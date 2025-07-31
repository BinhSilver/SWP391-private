<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Trang Chủ - Wasabii</title>

        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap" />
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>" />
    </head>

    <body>
        <div class="page-wrapper">
            <%@ include file="Home/nav.jsp" %>

        <!-- Advertisement Banner -->
        <%@ include file="ads.jsp"%>

            <!-- Hero Section -->
            <section class="hero bg-light py-5">
                <div class="container d-flex align-items-center justify-content-between flex-wrap">
                    <div class="hero-text">
                        <h1 class="fw-bold text-primary">Chào mừng trở lại, 
                            <span class="text-danger">
                                <c:out value="${sessionScope.user.fullName}" default="Học viên" />
                            </span>!
                        </h1>
                        <p class="lead mt-3">Tiếp tục hành trình học tiếng Nhật cùng Wasabii!</p>
                        <div class="mt-4">
                            <a href="<c:url value='/Courses/MyCourses.jsp' />" class="btn btn-danger me-2">
                                <i class="fas fa-book-open"></i> Khóa học của bạn
                            </a>
                            <a href="<c:url value='/User/Profile.jsp' />" class="btn btn-outline-secondary">
                                <i class="fas fa-user-circle"></i> Hồ sơ cá nhân
                            </a>
                        </div>
                    </div>
                    <div class="hero-image mt-4 mt-md-0">
                        <img src="<c:url value='/image/user-home.jpg'/>" alt="Wasabii Learning" class="img-fluid rounded-4" style="max-width: 450px;" />
                    </div>
                </div>
            </section>

            <section class="featured-courses py-5">
                <div class="container">
                    <h2 class="mb-4 text-center text-primary fw-bold">Gợi ý cho bạn</h2>
                    <div class="course-grid">
                        <c:forEach var="course" items="${suggestedCourses}">
                            <div class="course-card">
                                <h4>${course.title}</h4>
                                <p>${course.description}</p>
                                <div class="course-meta">${course.duration}</div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </section>



            <%@ include file="Home/footer.jsp" %>
        </div>

        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>
