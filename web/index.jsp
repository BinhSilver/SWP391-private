<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Wasabii - Học tiếng Nhật</title>

        <!-- ===== CSS & FONTS ===== -->
        <!-- Bootstrap CSS - Framework UI chính -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome - Icons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <!-- Google Fonts - JetBrains Mono -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <!-- Custom CSS files -->
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>">
    </head>
    <body>

        <!-- ===== MAIN PAGE WRAPPER ===== -->
        <div class="page-wrapper">
            
            <!-- ===== NAVIGATION BAR ===== -->
            <!-- Include navigation bar từ Home/nav.jsp -->
            <%@ include file="Home/nav.jsp" %>  
            
            <!-- ===== ADVERTISEMENT BANNER ===== -->
            <!-- Include quảng cáo cho Free users (roleID = 1) -->
            <%@ include file="ads.jsp"%>
            
            <!-- ===== CHAT BOX ===== -->
            <!-- Include chat box component -->
            <%@ include file="chatBoxjsp/chatBox.jsp" %>
            
            <!-- ===== HERO SECTION ===== -->
            <!-- Phần giới thiệu chính của trang -->
            <section class="hero">
                <div class="container">
                    <!-- Hero text content -->
                    <div class="hero-text">
                        <h1>Học tiếng Nhật cùng Wasabii</h1>
                        <p>Nền tảng học tiếng Nhật hiệu quả, tương tác, và dễ dàng.</p>
                        <!-- Call-to-action buttons -->
                        <a href="<c:url value='/Courses/Course.jsp'/>" class="btn-primary">Bắt đầu ngay</a>
                        <a href="<c:url value='/introduce.jsp'/>" class="btn-secondary">Tìm hiểu thêm</a>
                    </div>
                    <!-- Hero image -->
                    <div class="hero-image">
                        <img src="<c:url value='/image/homepage.jpg'/>" alt="Học tiếng Nhật">
                    </div>
                </div>
            </section>

            <!-- ===== FEATURED COURSES SECTION ===== -->
            <!-- Hiển thị các khóa học nổi bật -->
            <section class="featured-courses">
                <div class="container">
                    <h2>Khóa học nổi bật</h2>
                    <div class="course-grid">
                        <!-- ===== COURSE LOOP ===== -->
                        <!-- Lặp qua danh sách khóa học được đề xuất từ servlet -->
                        <c:forEach var="course" items="${suggestedCourses}">
                            <!-- Course card cho từng khóa học -->
                            <div class="course-card suggested">
                                <h4>${course.title}</h4>
                                <p>${course.description}</p>
                                <div class="course-meta text-muted">Khóa học đề xuất</div>
                                <!-- Link đến trang chi tiết khóa học -->
                                <a href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}" class="btn btn-outline-primary mt-2">
                                    <i class="fa-solid fa-arrow-right"></i> Xem chi tiết
                                </a>
                            </div>
                        </c:forEach>
                        <!-- ===== END COURSE LOOP ===== -->
                    </div>
                </div>
            </section>

            <!-- ===== FOOTER ===== -->
            <!-- Include footer từ Home/footer.jsp -->
            <%@ include file="Home/footer.jsp" %>

        </div>
        <!-- ===== END PAGE WRAPPER ===== -->

        <!-- ===== JAVASCRIPT LIBRARIES ===== -->
        <!-- Bootstrap JS - Framework UI -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Ionicons - Icon library -->
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <!-- Custom cherry blossom animation -->
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>