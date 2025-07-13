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

            <!-- Danh sách khóa học -->
            <section class="course-list container my-5">
                <h2>Danh Sách Khóa Học</h2>
                <c:if test="${empty courses}">
                    <div class="alert alert-info mt-4">Không có khóa học nào được tìm thấy.</div>
                </c:if>

                <c:if test="${not empty courses}">
                    <div class="course-grid">
                        <c:forEach var="course" items="${courses}">
                            <div class="course-card">
                                <h4>${course.title}</h4>
                                <p>${course.description}</p>
                                <!-- Thông tin meta cho giáo viên hoặc admin -->
                                <c:if test="${not empty sessionScope.user and (sessionScope.user.roleID == 3 or sessionScope.user.roleID == 4)}">
                                    <div class="course-meta small">
                                        <c:choose>
                                            <c:when test="${course.hidden}">
                                                <span class="badge bg-secondary">Đã ẩn</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success">Hiển thị công khai</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${course.suggested}">
                                            <span class="badge bg-info text-dark ms-2">Gợi ý trên trang chủ</span>
                                        </c:if>
                                    </div>
                                </c:if>

                                <!-- Nút chức năng dành cho giáo viên và admin -->
                                <c:if test="${not empty sessionScope.user and (sessionScope.user.roleID == 3 or sessionScope.user.roleID == 4)}">
                                    <div class="mt-2">
                                        <a href="<c:url value='/EditCourseServlet?id=${course.courseID}'/>" class="btn btn-sm btn-warning">
                                            <i class="fas fa-edit"></i> Sửa
                                        </a>
                                        <form action="DeleteCourseServlet" method="post" class="d-inline" onsubmit="return confirm('Bạn có chắc muốn xóa khóa học này?');">
                                            <input type="hidden" name="courseId" value="${course.courseID}" />
                                            <button type="submit" class="btn btn-sm btn-danger">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
                                        </form>
                                    </div>
                                </c:if>
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
