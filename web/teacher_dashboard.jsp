<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Wasabii - Teacher Dashboard</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/teacher_dashboard.css'/>">
        <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css" rel="stylesheet">

    </head>
    <body>
        <%@ include file="../Home/nav.jsp" %>

        <div class="page-wrapper">
            <div class="dashboard-wrapper">
                <!-- Sidebar -->
                <div class="sidebar">
                    <div class="sidebar-item">
                        <a href="#" class="main-link"><i class="fas fa-book"></i> Courses</a>
                        <div class="submenu">
                            <a href="<c:url value='/Statistic.jsp'/>"><i class="fas fa-chart-line"></i> Statistic</a>
                            <a href="<c:url value='/create_course.jsp'/>"><i class="fas fa-plus"></i> Create Course</a>
                        </div>
                    </div>
                    <div class="sidebar-item">
                        <a href="#" class="main-link"><i class="fas fa-user-graduate"></i> Student Statistic</a>
                        <div class="submenu">
                            <a href="#">Overview</a>
                            <a href="#">Performance</a>
                            <a href="#">Attendance</a>
                        </div>
                    </div>
                    <div class="sidebar-item">
                        <a href="#" class="main-link"><i class="fas fa-folder"></i> Materials</a>
                        <div class="submenu">
                            <a href="#">All Materials</a>
                            <a href="#">Add New</a>
                        </div>
                    </div>
                </div>

                <!-- Content -->
                <div class="content">
                    <h2>Sensei Tanaka <span style="font-size: 0.9rem; color: #666;">Teacher Dashboard</span></h2>
                    <p>Quản lý các khóa học tiếng Nhật của bạn</p>
                    <a href="<c:url value='/CreateCourseServlet'/>" class="btn-primary">+ Create New Course</a>

                    <!-- Hiển thị danh sách khóa học -->
                    <c:forEach var="course" items="${courses}">
                        <div class="course-item" id="course-${course.courseID}">
                            <div onclick="window.location.href = '<c:url value='/CourseDetailServlet'/>?id=${course.courseID}'" style="cursor: pointer;">
                                <h4>${course.title}
                                    <span style="color: #28a745;">
                                        <c:choose>
                                            <c:when test="${course.hidden}">Hidden</c:when>
                                            <c:otherwise>Visible</c:otherwise>
                                        </c:choose>
                                    </span>
                                </h4>
                                <p>${course.description}</p>
                            </div>
                            <div class="mt-2 d-flex gap-2">
                                <!-- Chuyển tới trang chỉnh sửa -->
                                <a href="<c:url value='/edit_course.jsp'/>?id=${course.courseID}" class="btn btn-sm btn-warning">
                                    <i class="fas fa-edit"></i> Edit
                                </a>

                                <!-- Nút Delete xử lý bằng JS -->
                                <button onclick="deleteCourse(${course.courseID})" class="btn btn-sm btn-danger">
                                    <i class="fas fa-trash"></i> Delete
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

        </div>
        <%@ include file="../Home/footer.jsp" %>

        <script>
            function deleteCourse(courseId) {
                Swal.fire({
                    title: 'Bạn có chắc chắn?',
                    text: "Hành động này sẽ xóa khóa học vĩnh viễn!",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#d33',
                    cancelButtonColor: '#3085d6',
                    confirmButtonText: 'Xóa',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch('<c:url value="/DeleteCourseServlet" />', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: 'courseId=' + encodeURIComponent(courseId)
                        })
                                .then(response => {
                                    if (response.ok) {
                                        // Hiệu ứng ẩn dòng đã xóa
                                        const courseDiv = document.getElementById("course-" + courseId);
                                        if (courseDiv) {
                                            courseDiv.classList.add("fade-out");
                                            setTimeout(() => courseDiv.remove(), 500);
                                        }

                                        Swal.fire(
                                                'Đã xóa!',
                                                'Khóa học đã được xóa thành công.',
                                                'success'
                                                );
                                    } else {
                                        Swal.fire(
                                                'Lỗi!',
                                                'Không thể xóa khóa học. Vui lòng thử lại.',
                                                'error'
                                                );
                                    }
                                })
                                .catch(error => {
                                    console.error("Lỗi:", error);
                                    Swal.fire(
                                            'Lỗi!',
                                            'Đã xảy ra lỗi trong quá trình gửi yêu cầu.',
                                            'error'
                                            );
                                });
                    }
                });
            }
        </script>


        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    </body>
</html>
