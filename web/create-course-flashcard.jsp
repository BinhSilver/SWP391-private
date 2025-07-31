<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Flashcard từ Khóa học - Wasabii</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    <style>
        .course-info {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .course-image {
            width: 100%;
            max-height: 200px;
            object-fit: cover;
            border-radius: 8px;
        }
        
        .flashcard-info {
            background-color: #e9f7ef;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .action-buttons {
            margin-top: 30px;
        }
        
        .alert {
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <%@ include file="Home/nav.jsp" %>
    
        <!-- Advertisement Banner -->
        <%@ include file="ads.jsp"%>
    
    <div class="container mt-5 mb-5">
        <h1 class="text-center mb-4">Tạo Flashcard từ Khóa học</h1>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
        </c:if>
        
        <div class="row">
            <div class="col-md-6">
                <div class="course-info">
                    <h3>Thông tin khóa học</h3>
                    <c:if test="${not empty course.imageUrl}">
                        <img src="${course.imageUrl}" alt="${course.title}" class="course-image mb-3">
                    </c:if>
                    <h4>${course.title}</h4>
                    <p>${course.description}</p>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="flashcard-info">
                    <h3>Thông tin Flashcard</h3>
                    <p>
                        <i class="fas fa-info-circle"></i> 
                        Flashcard sẽ được tạo tự động từ từ vựng của khóa học này.
                    </p>
                    <ul>
                        <li>Mỗi từ vựng sẽ trở thành một thẻ flashcard</li>
                        <li>Mặt trước: Từ tiếng Nhật</li>
                        <li>Mặt sau: Nghĩa, cách đọc, ví dụ và hình ảnh (nếu có)</li>
                        <li>Flashcard sẽ được công khai cho tất cả học viên đăng ký khóa học</li>
                    </ul>
                    
                    <c:choose>
                        <c:when test="${hasFlashcard}">
                            <div class="alert alert-info" role="alert">
                                <i class="fas fa-check-circle"></i> 
                                Khóa học này đã có flashcard. Bạn có thể xem flashcard hiện có hoặc tạo mới.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning" role="alert">
                                <i class="fas fa-exclamation-triangle"></i>
                                Khóa học này chưa có flashcard. Hãy tạo flashcard để giúp học viên học từ vựng hiệu quả hơn.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <div class="action-buttons text-center">
            <c:choose>
                <c:when test="${hasFlashcard}">
                    <a href="view-flashcard?id=${flashcardId}" class="btn btn-primary btn-lg me-3">
                        <i class="fas fa-eye"></i> Xem Flashcard hiện có
                    </a>
                    <form action="create-course-flashcard" method="post" style="display: inline-block;">
                        <input type="hidden" name="courseId" value="${course.courseID}">
                        <button type="submit" class="btn btn-warning btn-lg">
                            <i class="fas fa-sync-alt"></i> Tạo lại Flashcard
                        </button>
                    </form>
                </c:when>
                <c:otherwise>
                    <form action="create-course-flashcard" method="post">
                        <input type="hidden" name="courseId" value="${course.courseID}">
                        <button type="submit" class="btn btn-success btn-lg">
                            <i class="fas fa-plus-circle"></i> Tạo Flashcard
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>
            
            <a href="CourseDetailServlet?id=${course.courseID}" class="btn btn-secondary btn-lg ms-3">
                <i class="fas fa-arrow-left"></i> Quay lại khóa học
            </a>
        </div>
    </div>
    
    <%@ include file="Home/footer.jsp" %>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 