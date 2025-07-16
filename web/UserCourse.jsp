<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Wasabii - Courses</title>

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/user_course.css'/>">
    </head>
    <body>
        <%@ include file="../Home/nav.jsp" %>

        <div class="page-wrapper">
            <section class="featured-courses">
                <div class="container">
                    <h2>Khóa học tiếng Nhật nổi bật</h2>

                    <div class="course-grid row g-4">
                        <c:forEach var="course" items="${courses}">
                            <div class="col-md-4">
                                <div class="course-card card h-100" onclick="window.location.href = '<c:url value='/CourseDetailServlet'/>?id=${course.courseID}'" style="cursor:pointer;">
                                    <div class="course-thumbnail">
                                        <c:choose>
                                            <c:when test="${empty course.imageUrl}">
                                                <img src="<c:url value='/image/N5thumbnail.jpg'/>" alt="${course.title}" class="card-img-top" />
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${course.imageUrl}" alt="${course.title}" class="card-img-top" />
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-body">
                                        <h4 class="card-title">${course.title}</h4>
                                        <p class="card-text">${course.description}</p>
                                    </div>
                                    <div class="card-footer">
                                        <c:choose>
                                            <c:when test="${course.hidden}">
                                                <span class="badge bg-secondary">Hidden</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success">Visible</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </section>
        </div>

        <%@ include file="../Home/footer.jsp" %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
