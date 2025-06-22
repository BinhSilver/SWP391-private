<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Wasabii - Học tiếng Nhật</title>

        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>">
    </head>
    <body>

        <div class="page-wrapper">
            <%@ include file="Home/nav.jsp" %>

            <%@ include file="chatBoxjsp/chatBox.jsp" %>
            <!-- Hero Section -->
            <section class="hero">
                <div class="container">
                    <div class="hero-text">
                        <h1>Học tiếng Nhật cùng Wasabii</h1>
                        <p>Nền tảng học tiếng Nhật hiệu quả, tương tác, và dễ dàng.</p>
                        <a href="<c:url value='/Courses/Course.jsp'/>" class="btn-primary">Bắt đầu ngay</a>
                        <a href="#" class="btn-secondary">Tìm hiểu thêm</a>
                    </div>
                    <div class="hero-image">
                        <img src="<c:url value='/image/homepage.jpg'/>" alt="Học tiếng Nhật">
                    </div>
                </div>
            </section>

            <!-- Featured Courses -->
            <section class="featured-courses">
                <div class="container">
                    <h2>Khóa học nổi bật</h2>
                    <div class="course-grid">
                        <c:forEach var="course" items="${suggestedCourses}">
                            <div class="course-card suggested">
                                <h4>${course.title}</h4>
                                <p>${course.description}</p>
                                <div class="course-meta text-muted">Khóa học đề xuất</div>
                                <a href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}" class="btn btn-outline-primary mt-2">
                                    <i class="fa-solid fa-arrow-right"></i> Xem chi tiết
                                </a>

                            </div>
                        </c:forEach>

                    </div>
                </div>
            </section>



            <%@ include file="Home/footer.jsp" %>

        </div>


        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>