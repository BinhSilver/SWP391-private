<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Course" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết khóa học</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/course-detail.css'/>" />
        <link rel="stylesheet" href="<c:url value='/css/course-card.css'/>" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            iframe.pdf-frame {
                width: 100%;
                height: 1900px;
                border: none;
                border-radius: 12px;
                display: block;
                background-color: #fff;
                box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
            }
            
            /* Progress styles */
            .progress-container {
                margin: 20px 0;
            }
            
            .progress-bar {
                background-color: #e94f64; /* Thay đổi màu từ xanh sang hồng */
            }
            
            /* Các nút được style để giống với thiết kế mới */
            .btn-continue {
                background-color: #e94f64;
                color: white;
                border: none;
                padding: 8px 20px;
                border-radius: 5px;
                font-weight: 500;
                transition: all 0.3s;
                display: inline-flex;
                align-items: center;
                text-decoration: none;
                cursor: pointer;
            }
            
            .btn-continue:hover {
                background-color: #d43e55;
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(233, 79, 100, 0.25);
                text-decoration: none;
            }
            
            .btn-back {
                background-color: #f28faa;
                color: white;
                border: none;
                padding: 8px 20px;
                border-radius: 5px;
                font-weight: 500;
                transition: all 0.3s;
                display: inline-flex;
                align-items: center;
                text-decoration: none;
                cursor: pointer;
            }
            
            .btn-back:hover {
                background-color: #e17a95;
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(242, 143, 170, 0.25);
                text-decoration: none;
            }
            
            .btn-back i, .btn-continue i {
                margin-right: 8px;
            }
            
            /* Đảm bảo tiêu đề khóa học có màu hồng */
            .course-title {
                color: #e94f64 !important;
                font-size: 2rem;
                font-weight: 700;
                margin-bottom: 20px;
                transition: all 0.3s ease;
                letter-spacing: -0.5px;
            }
            
            .course-title:hover {
                color: #d43e55 !important;
                text-shadow: 0 0 1px rgba(233, 79, 100, 0.3);
            }
            
            .lesson-item {
                position: relative;
                border: 1px solid #eee;
                border-radius: 8px;
                margin-bottom: 10px;
                padding: 15px;
                transition: all 0.3s ease;
                background-color: #fff;
            }
            
            .lesson-item:hover {
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            }
            
            .lesson-icon {
                width: 30px;
                height: 30px;
                border-radius: 50%;
                background-color: #f8f9fa;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 10px;
            }
            
            .lesson-header {
                display: flex;
                align-items: center;
            }
            
            .lesson-title {
                margin: 0;
                flex: 1;
            }
            
            .lesson-status {
                display: flex;
                align-items: center;
            }
            
            .status-badge {
                padding: 5px 10px;
                border-radius: 20px;
                font-size: 0.8em;
                font-weight: bold;
                margin-left: 10px;
            }
            
            .status-completed {
                background-color: #d4edda;
                color: #155724;
            }
            
            .status-in-progress {
                background-color: #fff3cd;
                color: #856404;
            }
            
            .status-locked {
                background-color: #f8d7da;
                color: #721c24;
            }
            
            .lesson-content {
                margin-top: 10px;
                padding-left: 40px;
            }
            
            .lesson-actions {
                margin-top: 10px;
                display: flex;
                justify-content: flex-end;
            }
            
            .locked-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.05);
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 8px;
                z-index: 1;
            }
            
            .locked-message {
                background-color: rgba(255,255,255,0.9);
                padding: 10px 15px;
                border-radius: 5px;
                display: flex;
                align-items: center;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            }
            
            .locked-icon {
                color: #721c24;
                margin-right: 10px;
                font-size: 1.2em;
            }
        </style>
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <section class="container py-5">
                <c:choose>
                    <c:when test="${not empty course}">
                        <div class="course-card-container">
                            <h2 class="course-title">${course.title}</h2>
                            <p class="course-description">${course.description}</p>
                            
                            <c:if test="${currentUser != null && not empty overallProgress}">
                                <div class="progress-section">
                                    <p class="progress-title">Tiến độ học tập: ${overallProgress}%</p>
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar" 
                                             style="width: ${overallProgress}%;" 
                                             aria-valuenow="${overallProgress}" 
                                             aria-valuemin="0" 
                                             aria-valuemax="100">${overallProgress}%</div>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="course-status">
                                <p>Trạng thái:
                                    <c:choose>
                                        <c:when test="${course.hidden}">Ẩn</c:when>
                                        <c:otherwise>Hiển thị</c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="course-status-item">
                                    <c:choose>
                                        <c:when test="${course.suggested}">
                                            <i class="fas fa-check-circle"></i>
                                            Gợi ý: Có đề xuất
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-times-circle"></i>
                                            Gợi ý: Không
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <div class="course-actions">
                                <c:choose>
                                    <c:when test="${currentUser == null}">
                                        <a href="LoginServlet" class="btn-continue">
                                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${course.courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn-continue">
                                                    <i class="fas fa-book-open"></i> Học tiếp
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${!hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${lessons[0].courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn-continue">
                                                    <i class="fas fa-book-open"></i> Vào học
                                                </button>
                                            </form>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                                <a href="CoursesServlet" class="btn-back">
                                    <i class="fas fa-arrow-left"></i> Quay lại
                                </a>
                            </div>
                        </div>

                        <hr class="my-4">
                        <h4 class="text-dark">📘 Danh sách bài học</h4>

                        <div class="lesson-list mt-4">
                            <c:forEach var="lesson" items="${lessons}" varStatus="lessonStatus">
                                <div class="lesson-item">
                                    <c:if test="${currentUser != null && not empty lessonUnlockStatus && !lessonUnlockStatus[lesson.lessonID]}">
                                        <div class="locked-overlay">
                                            <div class="locked-message">
                                                <i class="fas fa-lock locked-icon"></i>
                                                <span>Hoàn thành bài học trước để mở khóa</span>
                                            </div>
                                        </div>
                                    </c:if>
                                    
                                    <div class="lesson-header">
                                        <div class="lesson-icon">
                                            <i class="fas fa-book"></i>
                                        </div>
                                        <h5 class="lesson-title">Bài ${lessonStatus.index + 1}: ${lesson.title}</h5>
                                        <div class="lesson-status">
                                            <c:choose>
                                                <c:when test="${not empty completedLessons && completedLessons.contains(lesson.lessonID)}">
                                                    <span class="status-badge status-completed">
                                                        <i class="fas fa-check-circle"></i> Hoàn thành
                                                    </span>
                                                </c:when>
                                                <c:when test="${not empty lessonCompletionMap && lessonCompletionMap[lesson.lessonID] > 0}">
                                                    <span class="status-badge status-in-progress">
                                                        <i class="fas fa-clock"></i> Đang học (${lessonCompletionMap[lesson.lessonID]}%)
                                                    </span>
                                                </c:when>
                                                <c:when test="${not empty lessonUnlockStatus && !lessonUnlockStatus[lesson.lessonID]}">
                                                    <span class="status-badge status-locked">
                                                        <i class="fas fa-lock"></i> Khóa
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                    
                                    <div class="lesson-content">
                                        <c:if test="${not empty lesson.description}">
                                            <p class="lesson-description">${lesson.description}</p>
                                        </c:if>
                                        
                                        <c:if test="${not empty lessonMaterialsMap[lesson.lessonID]}">
                                            <ul class="list-group list-group-flush mb-2">
                                                <c:forEach var="mat" items="${lessonMaterialsMap[lesson.lessonID]}">
                                                    <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                        <a href="${mat.filePath}" target="_blank">${mat.title}</a>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </c:if>
                                        
                                        <c:if test="${not empty quizMap[lesson.lessonID]}">
                                            <p>
                                                <i class="fas fa-question-circle"></i> 
                                                ${fn:length(quizMap[lesson.lessonID])} câu hỏi quiz
                                            </p>
                                        </c:if>
                                    </div>
                                    
                                    <div class="lesson-actions">
                                        <c:choose>
                                            <c:when test="${currentUser != null && not empty lessonUnlockStatus && lessonUnlockStatus[lesson.lessonID]}">
                                                <form action="StudyLessonServlet" method="get">
                                                    <input type="hidden" name="courseId" value="${lesson.courseID}" />
                                                    <input type="hidden" name="lessonId" value="${lesson.lessonID}" />
                                                    <button type="submit" class="btn-continue btn-sm">
                                                        <i class="fas fa-play-circle"></i> Học thử
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:when test="${currentUser == null}">
                                                <a href="LoginServlet" class="btn-continue btn-sm">
                                                    <i class="fas fa-lock"></i> Đăng nhập để học
                                                </a>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">
                            <h3>Không tìm thấy khóa học!</h3>
                            <p>Khóa học này không tồn tại hoặc đã bị xóa.</p>
                            <a href="CoursesServlet" class="btn-back mt-3">
                                <i class="fas fa-arrow-left"></i> Quay lại trang khóa học
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <jsp:include page="course-feedback-list.jsp" />

            <%@ include file="../Home/footer.jsp" %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
