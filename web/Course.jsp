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

            <c:if test="${not empty courses}">
                <div class="course-grid">
                    <c:forEach var="course" items="${courses}">
                        <div class="course-card">
                            <h4>${course.title}</h4>
                            <p>${course.description}</p>
                            <div class="mb-2">
                                <a href="CourseDetailServlet?id=${course.courseID}" class="btn btn-primary btn-sm">
                                    <i class="fas fa-eye"></i> Xem chi tiết
                                </a>
                                <c:if test="${currentUser != null && (currentUser.roleID == 4 || (currentUser.roleID == 3 && course.createdBy == currentUser.userID))}">
                                    <a href="edit_course.jsp?courseId=${course.courseID}" class="btn btn-warning btn-sm ms-1">
                                        <i class="fas fa-edit"></i> Sửa
                                    </a>
                                    <form action="DeleteCourseServlet" method="post" class="d-inline ms-1" onsubmit="return confirm('Bạn có chắc muốn xóa khóa học này?');">
                                        <input type="hidden" name="courseId" value="${course.courseID}" />
                                        <button type="submit" class="btn btn-danger btn-sm">
                                            <i class="fas fa-trash"></i> Xóa
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                            <div class="course-meta small mt-2">
                                <span class="badge ${course.hidden ? 'bg-secondary' : 'bg-success'}">
                                    ${course.hidden ? 'Đã ẩn' : 'Hiển thị công khai'}
                                </span>
                                <c:if test="${course.suggested}">
                                    <span class="badge bg-info text-dark ms-2">Gợi ý trên trang chủ</span>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <c:if test="${empty courses}">
                <div class="alert alert-info mt-4">Không có khóa học nào được tìm thấy.</div>
            </c:if>
        </section>

        <%@ include file="../Home/footer.jsp" %>
    </div>

    <!-- JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
