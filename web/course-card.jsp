<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khóa học của tôi</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/course-card.css'/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
    <div class="page-wrapper">
        <%@ include file="Home/nav.jsp" %>
        
        <!-- Advertisement Banner -->
        <%@ include file="ads.jsp"%>
        
        <div class="container py-5">
            <div class="row">
                <div class="col-md-10 mx-auto">
                    <!-- Card khóa học -->
                    <div class="course-card-container">
                        <h2 class="course-title">Khóa học Giao tiếp N5</h2>
                        <p class="course-description">Khóa học tiếng Nhật sơ cấp tập trung vào giao tiếp cơ bản.</p>
                        
                        <!-- Tiến độ học tập -->
                        <div class="progress-section">
                            <p class="progress-title">Tiến độ học tập: 66%</p>
                            <div class="progress">
                                <div class="progress-bar" role="progressbar" style="width: 66%;" aria-valuenow="66" aria-valuemin="0" aria-valuemax="100">66%</div>
                            </div>
                        </div>
                        
                        <!-- Trạng thái -->
                        <div class="course-status">
                            <p>Trạng thái: Hiển thị</p>
                            <p class="course-status-item">
                                <i class="fas fa-check-circle"></i>
                                Gợi ý: Có đề xuất
                            </p>
                        </div>
                        
                        <!-- Buttons -->
                        <div class="course-actions">
                            <a href="StudyLessonServlet?courseId=1&lessonId=1" class="btn-continue">
                                <i class="fas fa-book-open"></i> Học tiếp
                            </a>
                            <a href="HomeServlet" class="btn-back">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <%@ include file="Home/footer.jsp" %>
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 