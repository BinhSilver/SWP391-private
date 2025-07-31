<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Wasabii - Khóa Học Tiếng Nhật</title>
        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/course.css'/>">
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <!-- Advertisement Banner -->
            <%@ include file="ads.jsp"%>

            <!-- Danh sách khóa học -->
            <section class="course-list container my-5">
                <h2 class="text-danger fw-bold text-center mb-4">Danh Sách Khóa Học</h2>
                <c:if test="${empty courses}">
                    <div class="alert alert-info mt-4">Không có khóa học nào được tìm thấy.</div>
                </c:if>

                <c:if test="${not empty courses}">
                    <div class="course-grid">
                        <c:forEach var="course" items="${courses}">
                            <div class="course-card-horizontal">
                                <div class="course-thumb-wrap">
                                    <c:choose>
                                        <c:when test="${empty course.imageUrl}">
                                            <img class="course-thumb" src="/images/default_course.png" alt="Thumbnail" />
                                        </c:when>
                                        <c:otherwise>
                                            <img class="course-thumb" src="${course.imageUrl}" alt="Thumbnail" />
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="course-title-wrap">
                                    <a class="course-title-link" href="<c:url value='/CourseDetailServlet'/>?id=${course.courseID}">
                                        ${course.title}
                                    </a>
                                    <!-- Rating Display -->
                                    <div class="course-rating">
                                        <div class="stars">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star ${i <= course.averageRating ? 'filled' : 'empty'}">★</span>
                                            </c:forEach>
                                        </div>
                                        <div class="rating-info">
                                            <span class="average-rating">${course.averageRating > 0 ? String.format('%.1f', course.averageRating) : '0.0'}</span>
                                            <span class="rating-count">(${course.ratingCount} đánh giá)</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </section>
            <%@ include file="../Home/footer.jsp" %>
        </div>

        <!-- JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
