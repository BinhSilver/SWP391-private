<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Wasabii - Courses</title>

        <!-- Bootstrap, FontAwesome, Google Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="<c:url value='/css/course.css'/>">
    </head>
    <body>
        <%@ include file="../Home/nav.jsp" %>

        <div class="page-wrapper">
            <section class="featured-courses">
                <div class="container">
                    <h2>Khóa học tiếng Nhật nổi bật</h2>

                    <div class="course-grid">
                        <c:forEach var="course" items="${courses}">
                            <div class="course-card" onclick="window.location.href = '<c:url value='/CourseDetailServlet'/>?id=${course.courseID}'" style="cursor: pointer;">
                                <h4>${course.title}</h4>
                                <p>${course.description}</p>
                                <div class="course-meta">
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
                        </c:forEach>
                    </div>
                </div>
            </section>
        </div>

        <%@ include file="../Home/footer.jsp" %>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
